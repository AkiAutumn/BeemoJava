package me.beemo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static me.beemo.DiscordBot.*;

public class AuditLog extends ListenerAdapter {
    /*
    @Override
    public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {
        if(!sleeping) {
            System.out.println(event.getRawData());
        }
    }

    @Override
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
        if(!sleeping) {
            System.out.println(event.getRawData());
        }
    }
    */
    @Override
    public void onGuildBan(GuildBanEvent event) {
        if(!sleeping) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getUser().getName(), event.getUser().getEffectiveAvatarUrl(), event.getUser().getEffectiveAvatarUrl());
            embed.setTitle("Banned");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            embed.setFooter(dtf.format(now));
            embed.setColor(Color.pink);

            getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if(!sleeping) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl(), event.getMember().getEffectiveAvatarUrl());
            embed.setTitle("Member removed");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            embed.setFooter(dtf.format(now));
            embed.setColor(Color.pink);

            getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if(!sleeping) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl(), event.getMember().getEffectiveAvatarUrl());
            embed.addField("Role added:", roleString(event.getRoles()), false);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            embed.setFooter(dtf.format(now));
            embed.setColor(Color.pink);

            getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (!sleeping) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getMember().getEffectiveName(), event.getMember().getEffectiveAvatarUrl(), event.getMember().getEffectiveAvatarUrl());
            embed.addField("Role removed:", roleString(event.getRoles()), false);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            embed.setFooter(dtf.format(now));
            embed.setColor(Color.pink);

            getAuditLogChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    String roleString(java.util.List<Role> list) {
        ArrayList<String> roleArray = new ArrayList<>();
        for(Role role : list) {
            roleArray.add(role.getAsMention());
        }
        return String.join(", ", roleArray);
    }
}
