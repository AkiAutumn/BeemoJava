package me.beemo.commands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static me.beemo.DiscordBot.*;

public class vcJoinNotification extends ListenerAdapter {

    public static void vcJoinNotification(SlashCommandInteractionEvent event) throws IOException, ParseException {

        boolean enabled = event.getOption("enabled").getAsBoolean();
        String guildId = event.getGuild().getId();
        Member member = event.getMember();
        String userId = member.getId();

        if(!config.containsKey(guildId)) {
            config.put(guildId, new JSONObject());
        }
        JSONObject guildConfig = (JSONObject) config.get(guildId);

        if(!guildConfig.containsKey("channelNotifications")){
            guildConfig.put("channelNotifications", new JSONArray());
        }
        JSONArray channelNotifications = (JSONArray) guildConfig.get("channelNotifications");

        if(enabled){
            channelNotifications.add(userId);
            event.reply("You'll get notified whenever someone joins a VC on " + event.getGuild().getName()).setEphemeral(true).queue();
        } else {
            channelNotifications.remove(userId);
            event.reply("You won't get notified anymore!").setEphemeral(true).queue();
        }
        saveConfig();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        AudioChannel joinedChannel = event.getChannelJoined();
        JSONObject guildConfig = (JSONObject) config.get(event.getGuild().getId());
        if(!guildConfig.containsKey("channelNotifications")){
            guildConfig.put("channelNotifications", new JSONArray());
        }
        JSONArray channelNotifications = (JSONArray) guildConfig.get("channelNotifications");

        // Check if VC was joined
        if(joinedChannel != null) {
            for (Object channelNotification : channelNotifications) {
                String userId = (String) channelNotification;
                boolean cancelNotification = joinedChannel == guild.getAfkChannel();

                /*
                if(event.getGuild().getMemberById(userId).getOnlineStatus() != OnlineStatus.ONLINE) {
                    cancelNotification = true;
                }
                */

                if(!event.getGuild().getMemberById(userId).getPermissions(joinedChannel).contains(Permission.VIEW_CHANNEL)){
                    cancelNotification = true;
                }

                for(Member memberInVC : joinedChannel.getMembers()){
                    if (memberInVC.getId().equals(userId)){
                        cancelNotification = true;
                        break;
                    }
                }

                if(!cancelNotification) {

                    bot.retrieveUserById(userId).queue(user -> {
                        user.openPrivateChannel().queue((channel) ->
                        {
                            channel.sendMessage(member.getAsMention() + " joined " + joinedChannel.getAsMention() + "! :D").queue();
                        });
                    });

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


