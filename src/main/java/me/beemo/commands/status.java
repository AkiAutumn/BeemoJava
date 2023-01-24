package me.beemo.commands;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Array;

import static me.beemo.DiscordBot.*;

public class status {
    public static void updateBotStatus(String type, String content) throws IOException, ParseException {

        Activity activity = null;

        switch (type.toLowerCase()){
            case "watching":
                activity = Activity.watching(content);
                break;
            case "playing":
                activity = Activity.playing(content);
                break;
            case "listening":
                activity = Activity.listening(content);
                break;
            case "competing":
                activity = Activity.competing(content);
                break;
            case "custom":
                //activity = Activity.of(Activity.ActivityType.CUSTOM_STATUS, content);
                activity = Activity.of(Activity.ActivityType.COMPETING, content);
                break;
        }

        bot.getPresence().setActivity(activity);

        JSONArray array = new JSONArray();
        array.add(type);
        array.add(content);

        config.put("lastActivity", array);
        saveConfig();
    }
}
