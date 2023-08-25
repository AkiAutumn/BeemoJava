package me.beemo.commands.server_setup;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.beemo.DiscordBot.*;

public class joinToCreate extends ListenerAdapter {

    public static void joinToCreate(SlashCommandInteractionEvent event) throws IOException, ParseException {
        JSONObject guildConfigObject;

        if(config.get(event.getGuild().getId()) != null) {
            guildConfigObject = (JSONObject) config.get(event.getGuild().getId());
        } else {
            guildConfigObject = new JSONObject();
        }

        if(event.getOption("join-to-create").getAsChannel() == null) {
            guildConfigObject.put("joinToCreateChannelID", null);
            guildConfigObject.put("joinToCreateCategoryID", null);
            config.put(event.getGuild().getId(), guildConfigObject);
            saveConfig();
            event.reply("Not a voice channel. Removed join-to-create");
        } else {
            VoiceChannel voiceChannel = event.getOption("join-to-create").getAsChannel().asVoiceChannel();

            guildConfigObject.put("joinToCreateChannelID", voiceChannel.getId());
            guildConfigObject.put("joinToCreateCategoryID", voiceChannel.getParentCategory().getId());
            config.put(event.getGuild().getId(), guildConfigObject);
            saveConfig();
            event.reply(voiceChannel.getName() + " is now a join-to-create channel!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        Guild guild = event.getGuild();
        JSONObject guildConfig = (JSONObject) config.get(event.getGuild().getId());
        JSONArray tempChannels;

        if(guildConfig.get("temporaryVoiceChannels") == null){
            tempChannels = new JSONArray();
        } else {
            tempChannels = (JSONArray) guildConfig.get("temporaryVoiceChannels");
        }

        if(event.getChannelLeft() != null) {
            AudioChannel leftChannel = event.getChannelLeft();

            if (tempChannels.contains(leftChannel.getId())) {
                List<Member> memberList = leftChannel.getMembers();
                if (memberList.isEmpty()) {
                    leftChannel.delete().queue();
                    tempChannels.remove(leftChannel.getId());
                    guildConfig.put("temporaryVoiceChannels", tempChannels);
                    config.put(guild.getId(), guildConfig);

                }
            }
        }
    }
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        AudioChannel joinedChannel = event.getChannelJoined();
        AudioChannel leftChannel = event.getChannelLeft();
        JSONObject guildConfig = (JSONObject) config.get(event.getGuild().getId());

        JSONArray tempChannels;
        if(guildConfig.get("temporaryVoiceChannels") == null){
            tempChannels = new JSONArray();
        } else {
            tempChannels = (JSONArray) guildConfig.get("temporaryVoiceChannels");
        }

        // Check if VC was left
        if(leftChannel != null) {
            if (tempChannels.contains(leftChannel.getId())) {
                List<Member> memberList = leftChannel.getMembers();
                if (memberList.isEmpty()) {
                    if (leftChannel.getId().equals(String.valueOf(guildConfig.get("joinToCreateChannelID")))) {
                        guild.moveVoiceMember(member, leftChannel).queue();
                    } else {
                        leftChannel.delete().queue();
                        tempChannels.remove(leftChannel.getId());
                        guildConfig.put("temporaryVoiceChannels", tempChannels);
                        config.put(guild.getId(), guildConfig);
                    }
                }
            }
        }

        // Check if VC was joined
        if(joinedChannel != null) {
            if (joinedChannel.getId().equals(String.valueOf(guildConfig.get("joinToCreateChannelID")))) {
                Category category = guild.getCategoryById((String) guildConfig.get("joinToCreateCategoryID"));
                String channelName = member.getEffectiveName() + "'s Channel";
                boolean channelAlreadyExists = false;

                for(VoiceChannel voiceChannel : category.getVoiceChannels()){
                    if(voiceChannel.getName().equals(channelName)) {
                        channelAlreadyExists = true;
                    }
                }

                if(!channelAlreadyExists) {
                    category.createVoiceChannel(channelName).addPermissionOverride(member, Permission.MANAGE_CHANNEL.getRawValue(), Permission.UNKNOWN.getRawValue()).queue();
                }

                try {
                    Thread.sleep(1000);
                    VoiceChannel voiceChannel = guild.getVoiceChannelsByName(channelName, false).get(0);
                    guild.moveVoiceMember(member, voiceChannel).queue();

                    tempChannels.add(voiceChannel.getId());
                    guildConfig.put("temporaryVoiceChannels", tempChannels);
                    config.put(guild.getId(), guildConfig);

                    saveConfig();
                } catch (IOException | ParseException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //save config changes
        try {
            saveConfig();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
