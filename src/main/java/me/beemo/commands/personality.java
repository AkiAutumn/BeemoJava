package me.beemo.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.JSONObject;

import static me.beemo.DiscordBot.config;

public class personality {
    public static void beemoSetPersonality(SlashCommandInteractionEvent event, String personality){
        if(personality != null) {
            config.put("personality", personality);
            event.reply("I updated my personality!").setEphemeral(true).queue();
        } else {
            JSONObject jsonObject = (JSONObject) config.get("personality");
            if(jsonObject != null) {
                event.reply("Current personality: " + jsonObject.toJSONString()).setEphemeral(true).queue();
            } else {
                event.reply("No config entry for a personality. Using default personality.").setEphemeral(true).queue();
            }
        }
    }
}
