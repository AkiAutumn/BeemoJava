package me.beemo.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static me.beemo.DiscordBot.bot;
import static me.beemo.DiscordBot.humanReadableByteCountBin;

public class info {
    public static void beemoInfo(SlashCommandInteractionEvent event){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Bot Info");
        embed.addField("Rest Ping", bot.getGatewayPing() + " ms", false);
        embed.addField("Memory", humanReadableByteCountBin((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + " / " + humanReadableByteCountBin(Runtime.getRuntime().maxMemory()), false);
        embed.addField("Operating System", System.getProperty("os.name"), false);
        embed.addField("Java Version", System.getProperty("java.version"), true);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        embed.setFooter(dtf.format(now));
        embed.setColor(Color.cyan);
        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
