package me.beemo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.handle.GuildRoleUpdateHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static me.beemo.DiscordBot.*;
import static me.beemo.DiscordBot.humanReadableByteCountBin;

public class AuditLog extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Role(s) added");
        embed.setThumbnail(event.getMember().getAvatarUrl());
        embed.addField("User:", event.getMember().getAsMention(), true);
        embed.addField("Role(s):", event.getRoles().toString(), true);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        embed.setFooter(dtf.format(now));
        embed.setColor(Color.pink);

        getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Role(s) removed");
        embed.setThumbnail(event.getMember().getAvatarUrl());
        embed.addField("User:", event.getMember().getAsMention(), true);
        embed.addField("Role(s):", event.getRoles().toString(), true);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        embed.setFooter(dtf.format(now));
        embed.setColor(Color.pink);

        getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
