package me.beemo;

import io.github.cdimascio.dotenv.Dotenv;
import me.beemo.commands.colorMenu;
import me.beemo.commands.games;
import me.beemo.commands.makesurvey;
import me.beemo.commands.pronouns;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static me.beemo.commands.colorMenu.colorRoleCommand;
import static me.beemo.commands.games.gameRoleCommand;
import static me.beemo.commands.massmove.moveAll;
import static me.beemo.commands.pronouns.pronounsRoleCommand;
import static me.beemo.commands.say.say;
import static me.beemo.commands.status.status;
import static me.beemo.commands.wake.wake;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot extends ListenerAdapter {

    public static JDA bot;
    public static JSONObject config;

    public static void main(String[] args) throws ParseException, IOException {

        Dotenv dotenv = Dotenv.configure().load();
        config = (JSONObject) new JSONParser().parse(new FileReader("config.json"));

        bot = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                .addEventListeners(new DiscordBot())
                .addEventListeners(new pronouns())
                .addEventListeners(new colorMenu())
                .addEventListeners(new games())
                .addEventListeners(new makesurvey())
                .build();

        // These commands might take a few minutes to be active after creation/update/delete
        CommandListUpdateAction commands = bot.updateCommands();

        commands.addCommands(
                Commands.user("Shutdown")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.user("Update")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true), // you can add required options like this too
                Commands.slash("status", "Changes my status")
                        .addOptions(
                                new OptionData(OptionType.STRING, "type", "The type of activity", true)
                                        .addChoice("(Custom Text)", "custom")
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
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS))
        );
        // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
        commands.queue();

        System.out.println("Beemo on the line.");

        //get last activity status and set it again
        Object lastActivityObject = config.get("lastActivity");
        if(lastActivityObject != null){
            Activity lastActivity = (Activity) lastActivityObject;
            bot.getPresence().setActivity(lastActivity);
        }
    }

    public void onUserContextInteraction(UserContextInteractionEvent event)
    {
        switch (event.getName().toLowerCase()) {
            case "shutdown":
                event.reply("Killing myself now ... :(").setEphemeral(true).queue();
                bot.shutdown();
                break;
            case "update":
                try {
                    Runtime.getRuntime().exec("./update.sh");
                    event.reply("Updating myself now ... :D").setEphemeral(true).queue();
                    bot.shutdown();
                } catch (IOException e) {
                    event.reply("Update failed - " + e).setEphemeral(true).queue();
                    throw new RuntimeException(e);
                }
                break;
            default:
                event.reply("I don't recognise this command :(").setEphemeral(true).queue();
        }
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName())
        {
            case "say":
                say(event, event.getOption("content").getAsString()); // content is required so no null-check here
                break;
            case "status":
                status(event, event.getOption("type").getAsString(), event.getOption("content").getAsString());
                break;
            case "makesurvey":
                //onMessageReceived(); redundant function reworked, this line doesn't work anymore
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

        if(message.getMentions().getUsers().contains(bot.getSelfUser())){
            message.reply(beemoIdleReply()).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        event.deferEdit().queue();
    }

    public static String beemoIdleReply(){

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

    public static void writeToConfig(String key, Object object) throws IOException, ParseException {
        config.put(key, object);

        // writing JSON to file:"JSONExample.json" in cwd
        PrintWriter pw = new PrintWriter("config.json");
        pw.write(config.toJSONString());

        pw.flush();
        pw.close();

        config = (JSONObject) new JSONParser().parse(new FileReader("JSONExample.json"));
    }
}
