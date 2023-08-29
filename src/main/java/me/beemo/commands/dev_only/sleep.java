package me.beemo.commands.dev_only;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import static me.beemo.DiscordBot.*;

public class sleep {
    public static void toggleSleep(SlashCommandInteractionEvent event){
        if(botAdminList.contains(event.getUser().getId())) {
            if(sleeping) {
                sleeping = false;
                event.reply("Woken up! :D").setEphemeral(true).queue();
            } else {
                sleeping = true;
                event.reply("*Yawn* falling asleep...").setEphemeral(true).queue();
            }
        } else {
            event.reply("Im sorry, only my developers are allowed to do this!").setEphemeral(true).queue();
        }
    }
}
