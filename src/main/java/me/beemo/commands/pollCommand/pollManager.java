package me.beemo.commands.pollCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class pollManager extends ListenerAdapter {
    static ArrayList<Poll> activePolls = new ArrayList<>();

    public static void makePoll (SlashCommandInteractionEvent event, String title, String options) {

        String[] choices = options.split(",");
        boolean sameNameMenuExists = false;

        for (Poll poll : activePolls) {
            if (Objects.equals(poll.getTitle(), title)) {
                sameNameMenuExists = true;
                break;
            }
        }

        if(!sameNameMenuExists) {
            Collection<Button> buttons = new ArrayList<>();
            ArrayList<Choice> choiceArrayList = new ArrayList<>();

            for (String choice : choices) {
                String choiceId = title + "xyz" + choice;

                buttons.add(Button.secondary(event.getUser().getId() + ":" + choiceId, choice));
                choiceArrayList.add(new Choice(choiceId, choice));
            }

            MessageCreateAction messageCreateAction = event.getChannel().sendMessage(title).addActionRow(buttons);
            messageCreateAction.queue();
            event.reply("✓").setEphemeral(true).queue();

            Poll poll = new Poll(title, choiceArrayList, true);
            activePolls.add(poll);
        } else {
            event.reply("A poll with a similar title already exists. End it before you create another one with that name.").setEphemeral(true).queue();
        }
    }

    public static void endPoll (SlashCommandInteractionEvent event, String title) {
        for (Poll poll : activePolls) {
            if(poll.getTitle().equals(title)){
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Results of **" + title + "**");

                for(Choice choice : poll.getChoices()){
                    embed.addField(choice.getName(), String.valueOf(choice.getVotes()), false);
                }

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                embed.setFooter(dtf.format(now));
                embed.setColor(Color.cyan);
                event.replyEmbeds(embed.build()).queue();

                activePolls.remove(poll);
                break;
            }
        }
        event.reply("No such poll found").setEphemeral(true).queue();
    }

    public static void getPolls (SlashCommandInteractionEvent event) {

        int count = 0;
        String replyText = "**[active polls: " + activePolls.size() + "]**";

        for (Poll poll : activePolls) {
            String title = poll.getTitle();
            replyText += " " + title + ",";
            count++;
        }

        replyText = replyText.substring(0, replyText.length() - 1);

        if(count == 0){
            event.reply("No polls active!").setEphemeral(true).queue();
        }
        else {
            event.reply(replyText).setEphemeral(true).queue();
        }
    }



    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];

        for(Poll poll : activePolls){
            for (Choice choice : poll.getChoices()){
                if (Objects.equals(choice.getId(), type)) { //Clicked button equals existing active choice
                    if(!poll.isMultipleChoice()){
                        //TODO
                    }
                    if(!choice.hasVoted(authorId)) {
                        choice.addVote(authorId);
                        event.reply("Counted your vote!").setEphemeral(true).queue();
                    } else {
                        choice.removeVote(authorId);
                        event.reply("Removed your vote!").setEphemeral(true).queue();
                    }
                    break;
                } else {
                    event.reply("This poll has probably already ended... :(").setEphemeral(true).queue();
                }
            }
        }
    }
}
