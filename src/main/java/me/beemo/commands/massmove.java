package me.beemo.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public class massmove {

    public static void move(SlashCommandInteractionEvent event, Channel destination)
    {

        Member member = event.getMember();

        if (destination.getType().equals(ChannelType.VOICE)){
            AudioChannel audioChannel = (AudioChannel) destination;
            for(Member everyoneInChannel : member.getVoiceState().getChannel().getMembers()){
                event.getGuild().moveVoiceMember(everyoneInChannel, audioChannel).queue();
            }
            event.reply("Done :3").setEphemeral(true).queue();
        } else {
            event.reply("Wrong channel type :(").setEphemeral(true).queue();
        }
    }
}