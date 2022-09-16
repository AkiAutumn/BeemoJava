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

public class pronouns extends ListenerAdapter {
    public static void pronouns(SlashCommandInteractionEvent event)
    {
        String userId = event.getUser().getId();
        event.reply("Select your pronouns!") // prompt the user with a button menu
                .addActionRow(// this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
                        Button.secondary(userId + ":hehim", "He/Him"),
                        Button.secondary(userId + ":sheher", "She/Her"),
                        Button.secondary(userId + ":theythem", "They/Them"),
                        Button.secondary(userId + ":none", "None")) // the first parameter is the component id we use in onButtonInteraction above
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
        event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

        MessageChannel channel = event.getChannel();
        UserSnowflake userSnowflake = UserSnowflake.fromId(event.getUser().getId());
        Guild guild = event.getGuild();
        List<Role> memberRoles = event.getMember().getRoles();
        Role sheher = event.getGuild().getRoleById("1020412182728556554");
        Role hehim = event.getGuild().getRoleById("1020412126013182014");
        Role theythem = event.getGuild().getRoleById("1020412220510851243");
        Role roleToAdd = null;

        switch (type)
        {
            case "hehim":
                roleToAdd = hehim;
                break;
            case "sheher":
                roleToAdd = sheher;
                break;
            case "theythem":
                roleToAdd = theythem;
                break;
            case "none":
                roleToAdd = null;
                break;
        }

        if(memberRoles.contains(sheher) && sheher != roleToAdd){
            guild.removeRoleFromMember(userSnowflake, sheher);
        }
        if(memberRoles.contains(hehim) && hehim != roleToAdd){
            guild.removeRoleFromMember(userSnowflake, hehim);
        }
        if(memberRoles.contains(theythem) && theythem != roleToAdd){
            guild.removeRoleFromMember(userSnowflake, theythem);
        }
        if(!memberRoles.contains(roleToAdd) && roleToAdd != null){
            guild.addRoleToMember(userSnowflake, roleToAdd);
            System.out.println(event.getMember().getNickname() + " -> pronoun -> " + type);
        }
    }
}

