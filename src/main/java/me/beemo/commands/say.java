package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class say {
    public static void say(SlashCommandInteractionEvent event, String content)
    {
        event.getChannel().sendMessage(content).queue();
    }
}
