package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.reportToDeveloper;

public class joinToCreate {

    public static void joinToCreate(SlashCommandInteractionEvent event){
        reportToDeveloper(String.valueOf(event.getOption("category")));
    }
}
