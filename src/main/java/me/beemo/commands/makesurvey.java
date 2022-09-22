package me.beemo.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class makesurvey extends ListenerAdapter {
    public void onMessageReceived(MessageReactionAddEvent event) {
        if (event.getReaction().equals("📝")) {
            Message message = event.getChannel().getHistory().getMessageById(event.getMessageId());
            if (message != null) {
                message.addReaction(Emoji.fromUnicode("U+274C")).queue();
                message.addReaction(Emoji.fromUnicode("U+2705")).queue();
            }

        }
    }

    private void onMessageReceived(SlashCommandInteractionEvent event) {
    }

}
