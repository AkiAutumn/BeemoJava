package me.beemo.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class makesurvey extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        boolean check;
        if (event.getReaction().equals("📝")) {
            //when message has 📝 then continue
            check = true;
        } else {
            //when message has not 📝 then abort
            check = false;
        }

        if (event.getReaction().equals("✅")){
            //when message has ✅ then abort
            check = false;
        } else {
            //when message has not ✅ then continue
            check = true;
        }

        if (event.getReaction().equals("❌")){
            //when message has ❌ then abort
            check = false;
        } else {
            //when message has not ❌ then continue
            check = true;
        }

        if (check == true) {
            Message message = event.getChannel().getHistory().getMessageById(event.getMessageId());
            message.addReaction(Emoji.fromUnicode("U+274C")).queue();
            message.addReaction(Emoji.fromUnicode("U+2705")).queue();
            message.removeReaction(Emoji.fromUnicode("U+1F4DD")).queue();
        } else {
            return;
        }
    }
}
