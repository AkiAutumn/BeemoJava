package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.JSONObject;

import static me.beemo.DiscordBot.config;

public class personality {
    public static void beemoSetPersonality(SlashCommandInteractionEvent event){
        String newPersonality = null;
        JSONObject currentPersonality = (JSONObject) config.get("personality");

        if(event.getOption("new") != null) {
            newPersonality = event.getOption("new").getAsString();
        }

        if(newPersonality != null) {
            config.put("personality", newPersonality);
            event.reply("I updated my personality!").setEphemeral(true).queue();
        } else {
            if(currentPersonality != null) {
                event.reply("Current personality: " + currentPersonality.toJSONString()).setEphemeral(true).queue();
            } else {
                event.reply("No config entry for a personality. Using default personality.").setEphemeral(true).queue();
            }
        }
    }
}
