package me.beemo;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class DiscordBot {

    public static void main(String[] args){

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();

        JDA bot = JDABuilder.createDefault(dotenv.get("TOKEN"))
                .setActivity(Activity.watching("Cyberpunk Edgerunners"))
                .build();

    }
}
