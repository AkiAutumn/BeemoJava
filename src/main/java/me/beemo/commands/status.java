package me.beemo.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.bot;
import static me.beemo.DiscordBot.config;

public class status {
    public static void status(SlashCommandInteractionEvent event, String type, String content)
    {
        switch (type.toLowerCase()){
            case "watching":
                bot.getPresence().setActivity(Activity.watching(content));
                event.reply("Got it :3").setEphemeral(true).queue();
                break;
            case "playing":
                bot.getPresence().setActivity(Activity.playing(content));
                event.reply("Got it :3").setEphemeral(true).queue();
                break;
            case "listening":
                bot.getPresence().setActivity(Activity.listening(content));
                event.reply("Got it :3").setEphemeral(true).queue();
                break;
            case "competing":
                bot.getPresence().setActivity(Activity.competing(content));
                event.reply("Got it :3").setEphemeral(true).queue();
                break;
            case "custom":
                bot.getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, content));
                event.reply("Got it :3").setEphemeral(true).queue();
                break;
            default:
                event.reply("You entered an invalid activity type :(").setEphemeral(true).queue();
                break;
        }

        config.put("lastActivity", bot.getPresence().getActivity());
    }
}
