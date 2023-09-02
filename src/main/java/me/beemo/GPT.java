package me.beemo;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static me.beemo.DiscordBot.*;

public class GPT {
    public static ArrayList<String> context = new ArrayList<>();

    public static String chatGPT(String text, User user) throws IOException, ParseException {
        String API_KEY = Dotenv.configure().load().get("OPENAI");
        String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
        String CONTENT;

        JSONObject self = (JSONObject) config.get("self");
        if (self.get("personality") != null) {
            CONTENT = (String) self.get("personality");
        } else {
            CONTENT = "You are a Discord bot. You are inspired by BMO from Adventure Time."; //Default personality
            self.put("personality", CONTENT);
            config.put("self", self);
            saveConfig();
        }

        try {
            URL url = new URL(OPENAI_API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // Create the request body as per the API documentation
            String requestBody = "{\"messages\": [{\"role\": \"system\", \"content\": \"" + CONTENT + "For your memory, this is the past conversation: " + context.toString() + "\"}, " +
                    "{\"role\": \"user\", \"content\": \"" + text + "\"}], " +
                    "\"model\": \"gpt-3.5-turbo\", \"max_tokens\": 500}";

            connection.getOutputStream().write(requestBody.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Process the response

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response.toString());
                JSONObject choices = (JSONObject) ((JSONArray) json.get("choices")).get(0);
                JSONObject message = (JSONObject) choices.get("message");

                String output = message.get("content").toString();

                if(context.size() >= 10){
                    context.remove(context.get(0));
                    context.remove(context.get(1));
                }

                context.add(user.getName() + " : " + text + "\n");
                context.add("Beemo : " + output + "\n");

                return output;
            } else {
                connection.disconnect();
                return "```OpenAI API Request Failed. Response Code: " + responseCode + "```";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
