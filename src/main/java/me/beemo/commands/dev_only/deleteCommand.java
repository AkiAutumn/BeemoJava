package me.beemo.commands.dev_only;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.*;

public class deleteCommand {
    public static void deleteCommand(SlashCommandInteractionEvent event){
        if(botAdminList.contains(event.getUser().getId())) {
            String commandId = event.getOption("commandId").getAsString();

            bot.deleteCommandById(commandId).queue();
            event.reply("Queued command deletion!").setEphemeral(true).queue();
        } else {
            event.reply("Im sorry, only my developers are allowed to do this!").setEphemeral(true).queue();
        }
    }
}
