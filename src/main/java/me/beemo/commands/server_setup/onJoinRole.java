package me.beemo.commands.server_setup;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static me.beemo.DiscordBot.*;

public class onJoinRole extends ListenerAdapter {

    public static void onJoinRole(SlashCommandInteractionEvent event) throws IOException, ParseException {
        if(event.getOption("on-join-role").getAsRole() == null) {
            event.reply("Invalid role");
        } else {
            Role role = event.getOption("on-join-role").getAsRole();
            JSONObject guildConfigObject;
            if(config.get(event.getGuild().getId()) != null) {
                guildConfigObject = (JSONObject) config.get(event.getGuild().getId());
            } else {
                guildConfigObject = new JSONObject();
            }
            guildConfigObject.put("onJoinRoleID", role.getId());
            config.put(event.getGuild().getId(), guildConfigObject);
            saveConfig();
            event.reply("Every new member on the server will now get " + role.getAsMention() + "!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        JSONObject guildConfigObject;
        if(config.get(event.getGuild().getId()) != null) {
            guildConfigObject = (JSONObject) config.get(event.getGuild().getId());
        } else {
            guildConfigObject = new JSONObject();
        }

        String roleId = guildConfigObject.get("onJoinRoleID").toString();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        boolean roleExists = false;

        if(roleId != null){
            for(Role role : guild.getRoles()){
                if(role.getId().equals(roleId)){
                    roleExists = true;
                }
            }

            if(roleExists){
                Role role = event.getGuild().getRoleById(roleId);
                if(role.getAsMention() != "@everyone") {
                    guild.addRoleToMember(member, role).queue();
                }
            }
        }
    }
}
