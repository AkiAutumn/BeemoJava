package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.bot;
import static me.beemo.DiscordBot.getAuditLogChannel;

public class shutdown {
    public static void beemoShutdown(SlashCommandInteractionEvent event){
        getAuditLogChannel().sendMessage("Shutdown requested by " + event.getUser().getName()).queue();
        event.reply("Killing myself now ... :(").setEphemeral(true).queue();
        bot.shutdown();
    }
}
