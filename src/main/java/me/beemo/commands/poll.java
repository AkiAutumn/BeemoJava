package me.beemo.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.time.LocalDateTime;
import java.util.*;

public class poll extends ListenerAdapter {

    //TODO: create a poll object or idk

    static HashMap<String, ArrayList<String>> activePolls = new HashMap<>();

    public static void makePoll (SlashCommandInteractionEvent event, String title, String options, boolean multipleChoice) {

        String[] choices = options.split(",");
        boolean sameNameMenuExists = false;

        for (String key : activePolls.keySet()) {
            if (Objects.equals(key, title)) {
                sameNameMenuExists = true;
                break;
            }
        }

        if(!sameNameMenuExists) {
            Collection<Button> buttons = new ArrayList<>();
            ArrayList<String> choiceIds = new ArrayList<>();

            for (String choice : choices) {
                buttons.add(Button.secondary(title + ".separator." + choice, choice));
                choiceIds.add(title + ".separator." + choice);
            }

            MessageCreateAction messageCreateAction = event.getChannel().sendMessage(title).addActionRow(buttons);
            messageCreateAction.queue();
            event.reply("✓").setEphemeral(true).queue();

            activePolls.put(title, choiceIds);
        } else {
            event.reply("A poll with a similar title already exists. End it before you create another one with that name.").setEphemeral(true).queue();
        }
    }

    public static void endPoll (SlashCommandInteractionEvent event, String title) {
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {

    }
}
