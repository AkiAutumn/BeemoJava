package me.beemo.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.bot;

public class status {
    public static void status(SlashCommandInteractionEvent event, String type, String content)
    {

        switch (type.toLowerCase()){
            case "watching":
                bot.getPresence().setActivity(Activity.watching(content));
                event.reply("Got it :3").setEphemeral(true);
                break;
            case "playing":
                bot.getPresence().setActivity(Activity.playing(content));
                event.reply("Got it :3").setEphemeral(true);
                break;
            case "listening":
                bot.getPresence().setActivity(Activity.listening(content));
                event.reply("Got it :3").setEphemeral(true);
                break;
            case "competing":
                bot.getPresence().setActivity(Activity.competing(content));
                event.reply("Got it :3").setEphemeral(true);
                break;
            default:
                event.reply("You entered an invalid activity type :(").setEphemeral(true);
                break;
        }
    }
}
