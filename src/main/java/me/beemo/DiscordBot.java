package me.beemo;

import io.github.cdimascio.dotenv.Dotenv;
import me.beemo.commands.server_setup.joinToCreate;
import me.beemo.commands.server_setup.onJoinRole;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.EnumSet;

import static me.beemo.commands.colorMenu.colorRoleCommand;
import static me.beemo.commands.games.gameRoleCommand;
import static me.beemo.commands.info.beemoInfo;
import static me.beemo.commands.server_setup.joinToCreate.joinToCreate;
import static me.beemo.commands.server_setup.onJoinRole.onJoinRole;
import static me.beemo.commands.massmove.moveAll;
import static me.beemo.commands.personality.beemoSetPersonality;
import static me.beemo.commands.pronouns.pronounsRoleCommand;
import static me.beemo.commands.say.say;
import static me.beemo.commands.shutdown.beemoShutdown;
import static me.beemo.commands.status.updateBotStatus;
import static me.beemo.commands.update.beemoUpdate;
import static me.beemo.commands.wake.wake;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static JSONObject config;

    public static ArrayList<String> botAdminList = new ArrayList<>();

    public static void main(String[] args){
        try {
            Dotenv dotenv = Dotenv.configure().load();
            bot = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                    .addEventListeners(new DiscordBot())
                    .addEventListeners(new joinToCreate())
                    .addEventListeners(new onJoinRole())
                    .build();

            botAdminList.add("309307881205923840"); //Aki
            botAdminList.add("197424794063470592"); //Kumo

            // These commands might take a few minutes to be active after creation/update/delete
            CommandListUpdateAction commands = bot.updateCommands();

            commands.addCommands(
                    Commands.slash("shutdown", "Kill Beemo"),
                    Commands.slash("update", "Update Beemo"),
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
                            .setGuildOnly(true),
                    Commands.slash("server-setup", "Create various things")
                            .addOption(CHANNEL, "join-to-create", "Select the voice channel you want to use for join-to-create")
                            .addOption(ROLE, "on-join-role", "Select a role that every new member gets assigned automatically, when they join the server")
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );
            // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
            commands.queue();

            System.out.println("Beemo on the line.");

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
                    reportToDeveloper(getStackTrace(e));
                    throw new RuntimeException(e);
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
            case "shutdown":
                beemoShutdown(event);
                break;
            case "update":
                beemoUpdate(event);
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
                if(event.getOption("join-to-create") != null){
                    try {
                        joinToCreate(event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(event.getOption("on-join-role") != null){
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

    public void onGuildJoin(GuildJoinEvent event) {
        config.put(event.getGuild().getId(), new JSONObject());
        try {
            saveConfig();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (message.getMentions().getUsers().contains(bot.getSelfUser())) {
            String reply = null;
            message.getChannel().sendTyping().queue();
            try {
                reply = chatGPT(message.getContentDisplay());
                assert reply != null;
                message.reply(reply).queue();
            } catch (IOException | ParseException e) {
                reportToDeveloper(getStackTrace(e));
            }
        }
    }

    public static String chatGPT(String text) throws IOException, ParseException {
        String API_KEY = Dotenv.configure().load().get("OPENAI");
        String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
        String CONTENT;

        JSONObject self = (JSONObject) config.get("self");
        if (self.get("personality") != null) {
            CONTENT = (String) self.get("personality");
        } else {
            CONTENT = "You are a Discord bot. You are inspired by BMO from Adventure Time."; //Default personality
            self.put("personality", CONTENT);
            config.put("self", self);
            saveConfig();
        }

        try {
            URL url = new URL(OPENAI_API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // Create the request body as per the API documentation
            String requestBody = "{\"messages\": [{\"role\": \"system\", \"content\": \"" + CONTENT + "\"}, " +
                    "{\"role\": \"user\", \"content\": \"" + text + "\"}], " +
                    "\"model\": \"gpt-3.5-turbo\", \"max_tokens\": 500}";

            connection.getOutputStream().write(requestBody.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Process the response

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.toString());
                JSONObject choices = (JSONObject) ((JSONArray) json.get("choices")).get(0);
                JSONObject message = (JSONObject) choices.get("message");


                return message.get("content").toString();
            } else {
                connection.disconnect();
                return "OpenAI API Request Failed. Response Code: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
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
