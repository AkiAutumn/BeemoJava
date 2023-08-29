package me.beemo.commands.dev_only;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import static me.beemo.DiscordBot.config;
import static me.beemo.DiscordBot.saveConfig;

public class personality {
    public static void beemoSetPersonality(SlashCommandInteractionEvent event) throws IOException, ParseException {
        String newPersonality = null;

        if(event.getOption("new") != null) {
            newPersonality = event.getOption("new").getAsString();
        }

        JSONObject self = (JSONObject) config.get("self");

        if(newPersonality != null) {
            self.put("personality", newPersonality);
            config.put("self", self);
            saveConfig();
            event.reply("I updated my personality!").setEphemeral(true).queue();
        } else if(self.get("personality") != null) {
                String currentPersonality = (String) self.get("personality");
                event.reply("Current personality: " + currentPersonality).setEphemeral(true).queue();
            } else {
            event.reply("No config entry for a personality. Using default personality.").setEphemeral(true).queue();
        }
    }
}
