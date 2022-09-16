package me.beemo.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class wake {

    static HashMap<String, Long> wakyCooldown= new HashMap<String, Long>();
    public static void wake(SlashCommandInteractionEvent event, User user) throws InterruptedException {

        Member member = event.getGuild().getMemberById(user.getId());

        OptionMapping amountOption = event.getOption("amount"); // This is configured to be optional so check for null
        int amount = amountOption == null
                ? 1 // default 1
                : (int) Math.min(10, Math.max(1, amountOption.getAsLong())); // enforcement: must be between 1-10

        if(member != null && member.getVoiceState().inAudioChannel()) {
            if(member.getVoiceState().isDeafened()) {
                if (wakyCooldown.containsKey(user.getId())) {
                    if (wakyCooldown.get(user.getId()) > System.currentTimeMillis()) {
                        //still on cooldown
                        long time = (wakyCooldown.get(user.getId()) - System.currentTimeMillis()) / 1000;
                        event.reply(user.getName() + " is on waky-cooldown for " + time + " seconds >:(").setEphemeral(true).queue();
                    } else {
                        wakyCooldown.put(user.getId(), System.currentTimeMillis() + (amount * 10000));

                        for (int i = 0; i < amount; i++) {
                            AudioChannel afkChannel = event.getGuild().getAfkChannel();
                            AudioChannel currentChannel = member.getVoiceState().getChannel();

                            if (member.getVoiceState().isDeafened()) {
                                event.getGuild().moveVoiceMember(member, afkChannel).queue();
                                TimeUnit.SECONDS.sleep(1);
                                event.getGuild().moveVoiceMember(member, currentChannel).queue();
                            }
                        }
                    }
                } else {
                    wakyCooldown.put(user.getId(), System.currentTimeMillis() + (amount * 10000));

                    for (int i = 0; i < amount; i++) {
                        AudioChannel afkChannel = event.getGuild().getAfkChannel();
                        AudioChannel currentChannel = member.getVoiceState().getChannel();

                        if (member.getVoiceState().isDeafened()) {
                            event.getGuild().moveVoiceMember(member, afkChannel).queue();
                            TimeUnit.SECONDS.sleep(1);
                            event.getGuild().moveVoiceMember(member, currentChannel).queue();
                        }
                    }
                }

                event.reply("Tried to wake up " + user.getName() + " " + amount + " times ...").queue();
            } else {
                event.reply(user.getName() + " doesn't seem to be deafened").setEphemeral(true).queue();
            }
        } else {
            event.reply(user.getName() + " is not in a voice channel!").setEphemeral(true).queue();
        }
    }
}
