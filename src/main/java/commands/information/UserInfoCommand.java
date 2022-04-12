package commands.information;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public class UserInfoCommand extends SlashCommand {

    public UserInfoCommand() {
        this.name = "userinfo";
        this.help = "Informations about the User!";
        this.category = new Category("Information");

        this.options = Collections.singletonList(new OptionData(OptionType.USER, "member", "The member you want the avatar from").setRequired(false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(event.getOption("member") == null) {
            event.replyEmbeds(createEmbed(event.getMember())).queue();
        } else {
            event.replyEmbeds(createEmbed(event.getOption("member").getAsMember())).queue();
        }
    }

    enum discordFlags {
        DISCORD_EMPLOYEE("<:name:748954989656801361>"),
        DISCORD_PARTNER("<:name:748954989665189899>"),
        BUGHUNTER_LEVEL_1("<:name:748954989782630411>"),
        BUGHUNTER_LEVEL_2("<:name:748954989602537564>"),
        HYPESQUAD_EVENTS("<:name:748954989564657754>"),
        HYPESQUAD_BRAVERY("<:name:748954989623509062>"),
        HYPESQUAD_BRILLIANCE("<:name:748954989786955907>"),
        HYPESQUAD_BALANCE("<:name:748954989296091187>"),
        EARLY_SUPPORTER("<:name:748954989631766558>"),
        TEAM_USER("Team User"),
        HOUSE_EVENTS("House Events"),
        SYSTEM("<:name:749631258681671790>"),
        VERIFIED_BOT("<:name:749631258476413078>"),
        VERIFIED_DEVELOPER("<:name:748954989614989332>");

        private String name;
        discordFlags(String name) {
            this.name = name;
        }
        public String getFlagName() {
            return this.name;
        }
    }

    public MessageEmbed createEmbed(Member member) {

        EnumSet<User.UserFlag> userFlags = member.getUser().getFlags();

        MessageEmbed embed = new EmbedBuilder()
                //.setDescription("**User information about " + member.getUser().getAsTag() + "**")
                .setColor(member.getColor())
                .setThumbnail(member.getUser().getAvatarUrl())
                .addField("**User information about " + member.getUser().getAsTag() + "**",
                        "**❯ Username: **" + member.getUser().getAsTag()  +" \n" +
                                "**❯ ID: **" + member.getUser().getId()  +" \n" +
                                "**❯ Flags: **" + (!userFlags.isEmpty() ? flagB(member) : "None")  +" \n" +
                                "**❯ Avatar: ** [Link to Avatar](" + member.getUser().getAvatarUrl()  +") \n" +
                                "**❯ Time Created: **" + member.getTimeCreated().toLocalDate()
                        , true)
                .setTimestamp(OffsetDateTime.now())
                .build();
        return embed;
    }

    public String flagB(Member member) {
        StringBuilder flagsFinal = new StringBuilder();
        EnumSet<User.UserFlag> userFlag = member.getUser().getFlags();
        ArrayList<User.UserFlag> all = new ArrayList<User.UserFlag>(userFlag);

        for (User.UserFlag flag : all) {
            flagsFinal.append(", ");
            flagsFinal.append(discordFlags.valueOf(flag.toString()).getFlagName());
        }
        return flagsFinal.substring(2);
    }
}
