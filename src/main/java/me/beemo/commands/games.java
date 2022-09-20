package me.beemo.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class games extends ListenerAdapter {
    public static void gameRoleCommand(SlashCommandInteractionEvent event)
    {
        event.deferReply(true).queue();
        String userId = event.getUser().getId();

        event.getChannel().sendMessage(":video_game: Select the games you play, if you want to get notified about game sessions and upcoming projects! :video_game:" +
                        "                 \n *(Select the respective item again to remove the role)*") // prompt the user with a button menu
                .addActionRow(// this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
                        Button.secondary(userId + ":valorant", "VALORANT"),
                        Button.secondary(userId + ":minecraft", "Minecraft"),
                        Button.secondary(userId + ":amongus", "Among Us"))
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
        //if (!authorId.equals(event.getUser().getId()))
        //   return;

        MessageChannel channel = event.getChannel();
        UserSnowflake userSnowflake = UserSnowflake.fromId(event.getUser().getId());
        Guild guild = event.getGuild();
        List<Role> memberRoles = event.getMember().getRoles();
        Role valorantRole = event.getGuild().getRoleById("964945626754351114");
        Role minecraftRole = event.getGuild().getRoleById("788036628210778123");
        Role amongusRole = event.getGuild().getRoleById("951517928153559040");
        Role roleToAdd = null;

        switch (type)
        {
            case "valorant":
                roleToAdd = valorantRole;
                break;
            case "minecraft":
                roleToAdd = minecraftRole;
                break;
            case "amongus":
                roleToAdd = amongusRole;
                break;
        }

        if(!memberRoles.contains(roleToAdd) && roleToAdd != null){
            guild.addRoleToMember(userSnowflake, roleToAdd).queue();
        } else {
            guild.removeRoleFromMember(userSnowflake, roleToAdd).queue();
        }
    }
}

