package me.beemo.commands.dev_only;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static me.beemo.DiscordBot.botAdminList;

public class changeAPIKey {
    public static void changeKey(SlashCommandInteractionEvent event) {
        Dotenv dotenv = Dotenv.configure().load();
        String content = event.getOption("content").getAsString();
        String type = event.getOption("type").getAsString();

        if (botAdminList.contains(event.getUser().getId())) {
            switch (type) {
                case "openai":
                    try {
                        // input the (modified) file content to the StringBuffer "input"
                        BufferedReader file = new BufferedReader(new FileReader(".env"));
                        StringBuffer inputBuffer = new StringBuffer();
                        String line;

                        while ((line = file.readLine()) != null) {
                            if(line.startsWith("OPENAI")) {
                                line = "OPENAI=" + content;
                            }
                            inputBuffer.append(line);
                            inputBuffer.append('\n');
                        }
                        file.close();

                        // write the new string with the replaced line OVER the same file
                        FileOutputStream fileOut = new FileOutputStream(".env");
                        fileOut.write(inputBuffer.toString().getBytes());
                        fileOut.close();
                        event.reply("Successfully updated value!").setEphemeral(true).queue();
                    } catch (Exception e) {
                        System.out.println("Problem reading file.");
                    }

                    break;
            }
        } else {
            event.reply("Im sorry, only my developers are allowed to do this!").setEphemeral(true).queue();
        }
    }
}
