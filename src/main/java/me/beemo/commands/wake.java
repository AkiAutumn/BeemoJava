package me.beemo.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class wake {
    public static void wake(SlashCommandInteractionEvent event, User user){

        Member member = event.getGuild().getMemberById(user.getId());

        OptionMapping amountOption = event.getOption("amount"); // This is configured to be optional so check for null
        int amount = amountOption == null
                ? 1 // default 1
                : (int) Math.min(10, Math.max(1, amountOption.getAsLong())); // enforcement: must be between 1-10

        if(member != null && member.getVoiceState().inAudioChannel()) {

            for (int i = 0; i < amount; i++) {
                AudioChannel afkChannel = event.getGuild().getAfkChannel();
                AudioChannel currentChannel = member.getVoiceState().getChannel();

                event.getGuild().moveVoiceMember(member, afkChannel).queue();
                event.getGuild().moveVoiceMember(member, currentChannel).queue();
            }

            event.reply("Tried to wake up " + user.getName() + " " + amount + " times ...").queue();
        } else {
            event.reply(user.getName() + " is not in a voice channel!").setEphemeral(true).queue();
        }
    }
}
