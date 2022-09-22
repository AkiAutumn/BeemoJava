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
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.EnumSet;

import static me.beemo.commands.colorMenu.colorRoleCommand;
import static me.beemo.commands.games.gameRoleCommand;
import static me.beemo.commands.massmove.move;
import static me.beemo.commands.pronouns.pronounsRoleCommand;
import static me.beemo.commands.say.say;
import static me.beemo.commands.wake.wake;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DiscordBot extends ListenerAdapter {

    public static void main(String[] args){

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();

        JDA bot = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                .setActivity(Activity.watching("Cyberpunk Edgerunners"))
                .addEventListeners(new DiscordBot())
                .addEventListeners(new pronouns())
                .addEventListeners(new colorMenu())
                .addEventListeners(new games())
                .addEventListeners(new makesurvey())
                .build();

        // These commands might take a few minutes to be active after creation/update/delete
        CommandListUpdateAction commands = bot.updateCommands();

        commands.addCommands(
                Commands.slash("shutdown", "Terminates the active bot instance")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );
        commands.addCommands(
                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true) // you can add required options like this too
        );
        commands.addCommands(
                Commands.slash("pronouns", "Sends an embed for pronoun role assigning")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );
        commands.addCommands(
                Commands.slash("colors", "Sends an embed for color role assigning")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );
        commands.addCommands(
                Commands.slash("games", "Sends an embed for game role assigning")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );
        commands.addCommands(
                Commands.slash("move", "Moves all members of a channel")
                        .addOptions(new OptionData(CHANNEL, "destination", "Where to move", true).setChannelTypes(ChannelType.VOICE))
                        .setGuildOnly(true)
        );
        commands.addCommands(
                Commands.slash("wake", "Wakes deafened people")
                        .addOption(USER, "user", "Who to wake up", true)
                        .addOption(INTEGER, "amount", "How often to move")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MOVE_OTHERS))
        );
        // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
        commands.queue();

        System.out.println("Beemo on the line");
    }


    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName())
        {
            case "shutdown":
                event.reply("Killing myself now ... :c").setEphemeral(true).queue();
                System.exit(0);
                break;
            case "say":
                say(event, event.getOption("content").getAsString()); // content is required so no null-check here
                break;
            case "move":
                move(event, event.getOption("destination").getAsChannel());
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
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        event.deferEdit().queue();
    }
}
