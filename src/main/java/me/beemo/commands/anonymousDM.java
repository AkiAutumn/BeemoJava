package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.*;

public class anonymousDM {
    public static void sendAnonymousDM(SlashCommandInteractionEvent event){

        String userId = event.getOption("user").getAsUser().getId();
        String msg = event.getOption("message").getAsString();

        bot.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue((channel) ->
            {
                channel.sendMessage(msg).queue();
            });
        });
    }
}
