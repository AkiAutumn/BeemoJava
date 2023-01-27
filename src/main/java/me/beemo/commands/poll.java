package me.beemo.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.time.LocalDateTime;
import java.util.*;

public class poll extends ListenerAdapter {

    static ArrayList<SelectMenu> activePollMenus;

    public static void makePoll (SlashCommandInteractionEvent event) {
        String title = String.valueOf(event.getOption("title"));
        String options = String.valueOf(event.getOption("options"));
        int minChoices = event.getOption("minChoices").getAsInt();
        int maxChoices = event.getOption("maxChoices").getAsInt();
        long durationInMinutes = event.getOption("duration").getAsLong();

        String[] choices = options.split(",");
        Collection<SelectOption> menuOptions = new HashSet<>();

        for (String choice : choices) {
            menuOptions.add(SelectOption.of(choice, choice));
        }

        SelectMenu menu = SelectMenu.create(title)
                .setPlaceholder(title)
                .setRequiredRange(minChoices, maxChoices)
                .addOptions(menuOptions)
                .build();

        MessageCreateAction message = event.getChannel().sendMessage("").addActionRow(menu);
        message.queue();

        activePollMenus.add(menu);

        LocalDateTime startTime = LocalDateTime.now();

        Thread t = new Thread(new Runnable() {
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                if(startTime.plusMinutes(durationInMinutes).isBefore(now)){ //poll time is up
                    activePollMenus.remove(menu);
                }
            }
        });
        t.start();
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {



        if (event.getComponentId().equals("color-menu")) {
            String type = event.getValues().get(0);
        }
    }
}
