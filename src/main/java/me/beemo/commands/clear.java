package me.beemo.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class clear {
    public static void clear(SlashCommandInteractionEvent event, int amount)
    {
        if(amount > 100){
            event.reply("Due to API restrictions and general safety you can't delete more than 100 messages at a time.").setEphemeral(true).queue();
        } else {
            MessageHistory messageHistory = event.getChannel().getHistory();
            List<Message> pastMessages = messageHistory.retrievePast(amount).complete();

            for (Message message : pastMessages){
                message.delete().queue();
            }

            event.reply("Cleared " + amount + " messages! ^^").setEphemeral(true).queue();
        }
    }
}
