package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.config;

public class personality {
    public static void beemoSetPersonality(SlashCommandInteractionEvent event, String personality){
        config.put("personality", personality);
        event.reply("I updated my personality!").setEphemeral(true).queue();
    }
}
