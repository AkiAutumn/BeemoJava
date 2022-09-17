package me.beemo.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.ArrayList;
import java.util.List;

public class colorMenu extends ListenerAdapter {

    public static void colors(SlashCommandInteractionEvent event) {
        String userId = event.getUser().getId();

        SelectMenu menu = SelectMenu.create("color-menu")
                .setPlaceholder("(Click me)") // shows the placeholder indicating what this menu is for
                .setRequiredRange(1, 1) // only one can be selected
                .addOption("❤️ Red ❤️", "red")
                .addOption("\uD83E\uDDE1 Orange \uD83E\uDDE1", "orange")
                .addOption("\uD83D\uDC9B Yellow \uD83D\uDC9B", "yellow")
                .addOption("\uD83D\uDC9A Green \uD83D\uDC9A", "green")
                .addOption("\uD83D\uDC99\uD83E\uDD0D Light Blue \uD83E\uDD0D\uD83D\uDC99", "light_blue")
                .addOption("\uD83D\uDC99 Blue \uD83D\uDC99", "blue")
                .addOption("\uD83D\uDC9C Purple \uD83D\uDC9C", "purple")
                .addOption("\uD83D\uDC97 Pink \uD83D\uDC97", "pink")
                .addOption("\uD83E\uDD0D White \uD83E\uDD0D", "white")
                .addOption("\uD83D\uDDA4 Black \uD83D\uDDA4", "black")
                .addOption("None", "none")
                .build();

        event.getChannel().sendMessage(":rainbow: Select your color :rainbow:")
                .addActionRow(menu)
                .queue();
        event.reply("Did it :3").setEphemeral(true).queue();
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        if (event.getComponentId().equals("color-menu")) {
            String type = event.getValues().get(0);
            event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

            UserSnowflake userSnowflake = UserSnowflake.fromId(event.getUser().getId());
            Guild guild = event.getGuild();
            List<Role> memberRoles = event.getMember().getRoles();
            Role roleToAdd = null;

            switch (type) {
                case "red":
                    roleToAdd = guild.getRoleById("994330346491478147");
                    break;
                case "orange":
                    roleToAdd = guild.getRoleById("994328843835945010");
                    break;
                case "yellow":
                    roleToAdd = guild.getRoleById("994329869330686045");
                    break;
                case "green":
                    roleToAdd = guild.getRoleById("994329903363281026");
                    break;
                case "light_blue":
                    roleToAdd = guild.getRoleById("994702099730419866");
                    break;
                case "blue":
                    roleToAdd = guild.getRoleById("994329988679618590");
                    break;
                case "purple":
                    roleToAdd = guild.getRoleById("994329066360545380");
                    break;
                case "pink":
                    roleToAdd = guild.getRoleById("994330111639834704");
                    break;
                case "white":
                    roleToAdd = guild.getRoleById("994329515717300285");
                    break;
                case "black":
                    roleToAdd = guild.getRoleById("994329564534808577");
                    break;
            }

            ArrayList<String> otherColorRoleIds = new ArrayList<String>();
            otherColorRoleIds.add("994330346491478147");
            otherColorRoleIds.add("994328843835945010");
            otherColorRoleIds.add("994329869330686045");
            otherColorRoleIds.add("994329903363281026");
            otherColorRoleIds.add("994702099730419866");
            otherColorRoleIds.add("994329988679618590");
            otherColorRoleIds.add("994329066360545380");
            otherColorRoleIds.add("994330111639834704");
            otherColorRoleIds.add("994329515717300285");
            otherColorRoleIds.add("994329564534808577");

            for(String otherColorRoleId : otherColorRoleIds){
                Role otherColorRole = guild.getRoleById(otherColorRoleId);
                if (memberRoles.contains(otherColorRole) && roleToAdd != otherColorRole) {
                    guild.removeRoleFromMember(userSnowflake, otherColorRole).queue();
                }
            }

            if (!memberRoles.contains(roleToAdd) && roleToAdd != null) {
                guild.addRoleToMember(userSnowflake, roleToAdd).queue();
            }
        }
    }
}
