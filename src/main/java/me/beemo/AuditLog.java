package me.beemo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static me.beemo.DiscordBot.*;

public class AuditLog extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if(!sleeping) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Role added");
            embed.setImage(event.getMember().getAvatarUrl());
            embed.addField("User:", event.getMember().getAsMention(), true);
            embed.addField("Role:", roleString(event.getRoles()), true);
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
            embed.setTitle("Role removed");
            embed.setImage(event.getMember().getAvatarUrl());
            embed.addField("User:", event.getMember().getAsMention(), true);
            embed.addField("Role:", roleString(event.getRoles()), true);
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
