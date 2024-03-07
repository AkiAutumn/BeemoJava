package me.beemo.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class warn {
    public static void warn(SlashCommandInteractionEvent event, User user, String reason)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(user.getAsMention() + "has been warned");
        embed.addField("Reason", reason, false);
        embed.setColor(Color.red);
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
