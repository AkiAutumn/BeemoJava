package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.*;

public class shutdown {
    public static void beemoShutdown(SlashCommandInteractionEvent event){
        if(botAdminList.contains(event.getUser().getId())) {
            getAuditLogChannel().sendMessage("Shutdown requested by " + event.getUser().getName()).queue();
            event.reply("Killing myself now ... :(").setEphemeral(true).queue();
            bot.shutdown();
        } else {
            event.reply("Im sorry, only my developers are allowed to do this!").setEphemeral(true).queue();
        }
    }
}
