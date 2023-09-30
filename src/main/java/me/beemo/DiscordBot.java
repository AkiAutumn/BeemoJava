package me.beemo;

import io.github.cdimascio.dotenv.Dotenv;
import me.beemo.commands.pollCommand.pollManager;
import me.beemo.commands.server_setup.joinToCreate;
import me.beemo.commands.server_setup.onJoinRole;
import me.beemo.commands.vcJoinNotification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.EnumSet;

import static me.beemo.GPT.chatGPT;
import static me.beemo.commands.clear.clear;
import static me.beemo.commands.colorMenu.colorRoleCommand;
import static me.beemo.commands.dev_only.deleteCommand.deleteCommand;
import static me.beemo.commands.dev_only.sleep.toggleSleep;
import static me.beemo.commands.dev_only.changeAPIKey.changeKey;
import static me.beemo.commands.games.gameRoleCommand;
import static me.beemo.commands.info.beemoInfo;
import static me.beemo.commands.pollCommand.pollManager.endPoll;
import static me.beemo.commands.pollCommand.pollManager.makePoll;
import static me.beemo.commands.server_setup.joinToCreate.joinToCreate;
import static me.beemo.commands.server_setup.onJoinRole.onJoinRole;
import static me.beemo.commands.massmove.moveAll;
import static me.beemo.commands.dev_only.personality.beemoSetPersonality;
import static me.beemo.commands.pronouns.pronounsRoleCommand;
import static me.beemo.commands.say.say;
import static me.beemo.commands.dev_only.shutdown.beemoShutdown;
import static me.beemo.commands.status.updateBotStatus;
import static me.beemo.commands.dev_only.update.beemoUpdate;
import static me.beemo.commands.vcJoinNotification.vcJoinNotification;
import static me.beemo.commands.wake.wake;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static boolean sleeping = false;
    public static JSONObject config;

    public static ArrayList<String> botAdminList = new ArrayList<>();

    public static void main(String[] args){
        try {
            Dotenv dotenv = Dotenv.configure().load();
            bot = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                    .addEventListeners(new DiscordBot())
                    .addEventListeners(new joinToCreate())
                    .addEventListeners(new onJoinRole())
                    .addEventListeners(new pollManager())
                    .addEventListeners(new vcJoinNotification())
                    .build();

            System.out.println("Beemo on the line.");

            // Register bot admins
            botAdminList.add("309307881205923840"); //Aki
            botAdminList.add("700435712562168018"); //Sahne (Aki alt. account)
            botAdminList.add("197424794063470592"); //Kumo

            // These commands might take a few minutes to be active after creation/update/delete
            CommandListUpdateAction commands = bot.updateCommands();

            commands.addCommands(
                    Commands.slash("shutdown", "Kill Beemo"),
                    Commands.slash("sleep", "Disable all features"),
                    Commands.slash("update-key", "Update internal key or tokens")
                            .addOptions(
                                    new OptionData(OptionType.STRING, "type", "The key you want to update", true)
                                            .addChoice("OpenAI", "openai")
                            )
                            .addOption(STRING ,"content", "new key"),
                    Commands.slash("update", "Update Beemo"),
                    Commands.slash("delete-command", "Delete outdated commands"),
                    Commands.slash("info", "Get system info of Beemo's machine"),
                    Commands.slash("personality", "Change my personality")
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                            .addOption(STRING, "new", "Describe my new personality in a few sentences!"),
                    Commands.slash("say", "Makes the bot say what you tell it to")
                            .addOption(STRING, "content", "What the bot should say", true),
                    Commands.slash("status", "Changes my status")
                            .addOptions(
                                    new OptionData(OptionType.STRING, "type", "The type of activity", true)
                                            .addChoice("Watching ...", "watching")
                                            .addChoice("Playing ...", "playing")
                                            .addChoice("Listening to ...", "listening")
                                            .addChoice("Competing in ...", "competing")
                            )
                            .addOption(STRING, "content", "What the status should say", true),
                    Commands.slash("vc-notifications", "Get notified when someone joins a voice-channel. Make sure you enable DMs ;)")
                            .addOption(BOOLEAN, "enabled", "Toggle notifications", true),
                    Commands.slash("move-all", "Moves all members of a channel")
                            .addOptions(new OptionData(CHANNEL, "destination", "Where to move", true).setChannelTypes(ChannelType.VOICE))
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS)),
                    Commands.slash("wake", "Wakes deafened people")
                            .addOption(USER, "user", "Who to wake up", true)
                            .addOption(INTEGER, "amount", "How often to move")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS)),
                    Commands.slash("end-poll", "Ends a poll")
                            .addOption(STRING, "title", "The question or topic of the poll you wanna end", true)
                            .setGuildOnly(true),
                    Commands.slash("create-poll", "Sends a poll message")
                            .addOption(STRING, "title", "The question or topic the poll is about", true)
                            .addOption(STRING, "options", "Choices (separate by comma)", true)
                            //.addOption(BOOLEAN, "multiple-choice", "Are multiple choices allowed?")
                            .setGuildOnly(true),
                    Commands.slash("server-setup", "Create various things")
                            .addOption(CHANNEL, "join-to-create", "Select the voice channel you want to use for join-to-create")
                            .addOption(ROLE, "on-join-role", "Select a role that every new member gets assigned automatically, when they join the server")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("clear", "Delete previous messages")
                            .addOption(INTEGER, "amount", "How many messages to clear?", true)
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );
            // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
            commands.queue();

            //load config
            JSONParser parser = new JSONParser();
            try {
                config = (JSONObject) parser.parse(new FileReader("config.json"));
                if(config.get("self") == null){
                    config.put("self", new JSONObject());
                    saveConfig();
                }
            } catch (IOException | ParseException e) {
                reportToDeveloper(getStackTrace(e));
            }
            //get last activity status and set it again

            JSONObject self = (JSONObject) config.get("self");
            if(self != null) {
                JSONArray lastActivityArray = (JSONArray) self.get("lastActivity");
                if (lastActivityArray != null) {
                    try {
                        updateBotStatus(lastActivityArray.get(0).toString(), lastActivityArray.get(1).toString());
                    } catch (IOException | ParseException e) {
                        reportToDeveloper(getStackTrace(e));
                    }
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public static void reportToDeveloper(String message){
        bot.retrieveUserById("309307881205923840").queue(user -> {
            user.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage("```" + message + "```").queue();
            });
        });
    }

    public static TextChannel getAuditLogChannel(){
        return bot.getGuildById("425019763950092288").getTextChannelById("845732635350794270");
    }

    public void onMessageContextInteraction(MessageContextInteractionEvent event) {

    }

    public void onUserContextInteraction(UserContextInteractionEvent event) {

    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("sleep")){
            toggleSleep(event);
        }
        if(!sleeping) {
            // Only accept commands from guilds
            if (event.getGuild() == null)
                return;
            switch (event.getName()) {
                case "say":
                    say(event, event.getOption("content").getAsString()); // content is required so no null-check here
                    break;
                case "clear":
                    clear(event, event.getOption("amount").getAsInt()); // content is required so no null-check here
                    break;
                case "create-poll":
                    makePoll(event, event.getOption("title").getAsString(), event.getOption("options").getAsString()); // content is required so no null-check here
                    break;
                case "end-poll":
                    endPoll(event, event.getOption("title").getAsString()); // content is required so no null-check here
                    break;
                case "status":
                    try {
                        updateBotStatus(event.getOption("type").getAsString(), event.getOption("content").getAsString());
                        event.reply("Got it :3").setEphemeral(true).queue();
                    } catch (IOException | ParseException e) {
                        reportToDeveloper(getStackTrace(e));
                        throw new RuntimeException(e);
                    }
                    break;
                case "move-all":
                    moveAll(event, event.getOption("destination").getAsChannel());
                    break;
                case "vc-notifications":
                    try {
                        vcJoinNotification(event);
                    } catch (IOException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "pronouns":
                    pronounsRoleCommand(event);
                    break;
                case "colors":
                    colorRoleCommand(event);
                    break;
                case "games":
                    gameRoleCommand(event);
                    break;
                case "wake":
                    try {
                        wake(event, event.getOption("user").getAsUser());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "shutdown":
                    beemoShutdown(event);
                    break;
                case "update":
                    beemoUpdate(event);
                    break;
                case "update-key":
                    changeKey(event);
                    break;
                case "delete-command":
                    deleteCommand(event);
                    break;
                case "info":
                    beemoInfo(event);
                    break;
                case "personality":
                    try {
                        beemoSetPersonality(event);
                    } catch (IOException | ParseException e) {
                        reportToDeveloper(getStackTrace(e));
                    }
                    break;
                case "server-setup":
                    if (event.getOption("join-to-create") != null) {
                        try {
                            joinToCreate(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (event.getOption("on-join-role") != null) {
                        try {
                            onJoinRole(event);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                default:
                    event.reply("I don't recognise this command :(").setEphemeral(true).queue();
            }
        }
    }

    public void onGuildJoin(GuildJoinEvent event) {
        if(!sleeping) {
            config.put(event.getGuild().getId(), new JSONObject());
            try {
                saveConfig();
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if(!sleeping) {
            Message message = event.getMessage();

            if (message.getMentions().getUsers().contains(bot.getSelfUser())) {
                String reply = null;
                message.getChannel().sendTyping().queue();
                try {
                    reply = chatGPT(message.getContentDisplay(), event.getAuthor());
                    assert reply != null;
                    message.reply(reply).queue();
                } catch (IOException | ParseException e) {
                    reportToDeveloper(getStackTrace(e));
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!sleeping) {
            event.deferReply(true).queue();
        }
    }

    public static void saveConfig() throws IOException, ParseException {
        PrintWriter pw = new PrintWriter("config.json");
        pw.write(config.toJSONString());

        pw.flush();
        pw.close();
        config = (JSONObject) new JSONParser().parse(new FileReader("config.json"));
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
