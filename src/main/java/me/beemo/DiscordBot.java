package me.beemo;

import com.intellijava.core.controller.RemoteLanguageModel;
import com.intellijava.core.model.input.LanguageModelInput;
import io.github.cdimascio.dotenv.Dotenv;
import me.beemo.commands.colorMenu;
import me.beemo.commands.games;
import me.beemo.commands.pronouns;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

import static me.beemo.commands.colorMenu.colorRoleCommand;
import static me.beemo.commands.games.gameRoleCommand;
import static me.beemo.commands.massmove.moveAll;
import static me.beemo.commands.pronouns.pronounsRoleCommand;
import static me.beemo.commands.say.say;
import static me.beemo.commands.status.updateBotStatus;
import static me.beemo.commands.wake.wake;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static JSONObject config;

    public static void main(String[] args){

        try {
            Dotenv dotenv = Dotenv.configure().load();

            //load config

            JSONParser parser = new JSONParser();
            config = (JSONObject) parser.parse(new FileReader("config.json"));


            bot = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                    .addEventListeners(new DiscordBot())
                    .addEventListeners(new pronouns())
                    .addEventListeners(new colorMenu())
                    .addEventListeners(new games())
                    .build();

            // These commands might take a few minutes to be active after creation/update/delete
            CommandListUpdateAction commands = bot.updateCommands();

            commands.addCommands(
                    Commands.user("Shutdown")
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.user("Update")
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.user("Beemo Info"),
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
                    Commands.slash("pronouns", "Sends an embed for pronoun role assigning")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("colors", "Sends an embed for color role assigning")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("games", "Sends an embed for game role assigning")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                    Commands.slash("move-all", "Moves all members of a channel")
                            .addOptions(new OptionData(CHANNEL, "destination", "Where to move", true).setChannelTypes(ChannelType.VOICE))
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS)),
                    Commands.slash("wake", "Wakes deafened people")
                            .addOption(USER, "user", "Who to wake up", true)
                            .addOption(INTEGER, "amount", "How often to move")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS)),
                    Commands.slash("poll", "Sends a poll message")
                            .addOption(STRING, "options", "Choices (separate by comma)")
                            .setGuildOnly(true)
            );
            // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
            commands.queue();

            System.out.println("Beemo on the line.");
            getAuditLogChannel().sendMessage("Beemo deployed! >:3").queue();

            //get last activity status and set it again
            JSONArray lastActivityArray = (JSONArray) config.get("lastActivity");
            if (lastActivityArray != null) {
                updateBotStatus(lastActivityArray.get(0).toString(), lastActivityArray.get(1).toString());
                System.out.println("Successfully set last activity.");
            } else {
                System.out.println("Unable to set last activity.");
            }
        } catch (Exception e){
            try {
                getOnlyFansChannel().sendMessage(e.toString()).queue();
            } catch(Exception ignored){}
        }
    }

    public static TextChannel getOnlyFansChannel(){
        TextChannel textChannel = bot.getGuildById("425019763950092288").getTextChannelById("748137772501434498"); //our onlyfans channel
        return textChannel;
    }

    public static TextChannel getAuditLogChannel(){
        TextChannel textChannel = bot.getGuildById("425019763950092288").getTextChannelById("845732635350794270"); //our onlyfans channel
        return textChannel;
    }

    public void onMessageContextInteraction(MessageContextInteractionEvent event) {

    }

    public void onUserContextInteraction(UserContextInteractionEvent event) {
        switch (event.getName().toLowerCase()) {
            case "shutdown":
                event.reply("Killing myself now ... :(").setEphemeral(true).queue();
                bot.shutdown();
                break;
            case "update":
                try {
                    try {
                        getAuditLogChannel().sendMessage("Self-update requested by " + event.getUser().getName()).queue();
                    } catch(NullPointerException nullPointerException){
                        System.out.println(nullPointerException.toString());
                    }
                    Runtime.getRuntime().exec("./update.sh");
                    event.reply("Updating myself now ... :D").setEphemeral(true).queue();
                    bot.shutdown();
                    System.exit(0);
                } catch (IOException e) {
                    event.reply("Update failed: " + e).setEphemeral(true).queue();
                    throw new RuntimeException(e);
                }
                break;
            case "beemo info":
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Bot Info");
                embed.addField("Rest Ping", bot.getGatewayPing() + " ms", false);
                embed.addField("Memory", humanReadableByteCountBin((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + " / " + humanReadableByteCountBin(Runtime.getRuntime().maxMemory()), false);
                embed.addField("Operating System", System.getProperty("os.name"), false);
                embed.addField("Java Version", System.getProperty("java.version"), true);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                embed.setFooter(dtf.format(now));
                embed.setColor(Color.cyan);
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
                break;
            default:
                event.reply("I don't recognise this command :(").setEphemeral(true).queue();
        }
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName()) {
            case "say":
                say(event, event.getOption("content").getAsString()); // content is required so no null-check here
                break;
            case "status":
                try {
                    updateBotStatus(event.getOption("type").getAsString(), event.getOption("content").getAsString());
                    event.reply("Got it :3").setEphemeral(true).queue();
                } catch (IOException | ParseException e) {
                    System.out.println("Status Command: " + e);
                }
                break;
            case "move-all":
                moveAll(event, event.getOption("destination").getAsChannel());
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
            default:
                event.reply("I don't recognise this command :(").setEphemeral(true).queue();
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getMentions().getUsers().contains(bot.getSelfUser())) {
            String reply = null;
            try {
                message.getChannel().sendTyping().queue();
                reply = chatGPT(message.getContentDisplay());
                message.reply(reply).queue();
            } catch (Exception e) {
                getOnlyFansChannel().sendMessage(e.toString()).queue();
            }
        }
    }

    public static String chatGPT(String text) throws Exception {
        RemoteLanguageModel model = new RemoteLanguageModel("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", "openai");

        LanguageModelInput input = new LanguageModelInput
                .Builder(text)
                .setMaxTokens(250).setModel("text-davinci-003").setTemperature(0.5f).build();

        return model.generateText(input);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
    }

    public static String beemoIdleReply() {

        int randomNumber = ThreadLocalRandom.current().nextInt(0, 13 + 1);

        String[] replies = {
                "Beep",
                "Boop",
                "Boob",
                "Hi",
                "Whassaaap",
                "Huh?",
                "*triggered*",
                "Error",
                "java.lang.NullPointerException",
                "?",
                "!?",
                "Baumkuchen",
                "Für Fortnite!",
                "Daddy?",
                "Mommy?"
        };

        return replies[randomNumber];
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
}
