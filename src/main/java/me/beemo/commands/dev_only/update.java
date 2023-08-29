package me.beemo.commands.dev_only;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;

import static me.beemo.DiscordBot.*;

public class update {

    public static void beemoUpdate(SlashCommandInteractionEvent event){
        if(botAdminList.contains(event.getUser().getId())) {
            try {
                try {
                    getAuditLogChannel().sendMessage("Self-update requested by " + event.getUser().getName()).queue();
                } catch(NullPointerException nullPointerException){
                    System.out.println(nullPointerException.toString());
                }
                Runtime.getRuntime().exec("./update.sh");
                event.reply("Updating myself now ... :D").setEphemeral(true).queue();
                bot.shutdown();
                System.exit(0);
            } catch (IOException e) {
                event.reply("Update failed: " + e).setEphemeral(true).queue();
                reportToDeveloper(getStackTrace(e));
                throw new RuntimeException(e);
            }
        } else {
            event.reply("Im sorry, only my developers are allowed to do this!").setEphemeral(true).queue();
        }
    }
}
