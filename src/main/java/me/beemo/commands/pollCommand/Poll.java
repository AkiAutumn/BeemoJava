package me.beemo.commands.pollCommand;

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

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

public class Poll {
    private String title;
    private ArrayList<Choice> choices;
    private boolean multipleChoice;

    public Poll(String name, ArrayList<Choice> choiceArrayList, boolean isMultipleChoice) {
        title = name;
        choices = choiceArrayList;
        multipleChoice = isMultipleChoice;
    }

    public String getTitle(){
        return this.title;
    }

    public ArrayList<Choice> getChoices(){
        return this.choices;
    }

    public boolean isMultipleChoice(){
        return this.multipleChoice;
    }
}
