package commands.information;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.EnumSet;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "userinfo";
        this.help = "Informations about the User!";
        this.category = new Category("Information");
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            event.reply(createEmbed(event.getMessage().getMember()));
        } else {
            event.reply(createEmbed(event.getMessage().getMentionedMembers().get(0)));
        }
    }

    enum discordFlags {
        DISCORD_EMPLOYEE("<:name:748954989656801361>"),
        DISCORD_PARTNER("<:name:748954989665189899>"),
        BUGHUNTER_LEVEL_1("<:name:748954989782630411>"),
        BUGHUNTER_LEVEL_2("<:name:748954989602537564>"),
        HYPESQUAD_EVENTS("<:name:748954989564657754>"),
        HOUSE_BRAVERY("<:name:748954989623509062>"),
        HOUSE_BRILLIANCE("<:name:748954989786955907>"),
        HOUSE_BALANCE("<:name:748954989296091187>"),
        EARLY_SUPPORTER("<:name:748954989631766558>"),
        TEAM_USER("Team User"),
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
                                "**❯ Flags: **" + (!userFlags.isEmpty() ? flagBuilder(member) : "None")  +" \n" +
                                "**❯ Avatar: ** [Link to Avatar](" + member.getUser().getAvatarUrl()  +") \n" +
                                "**❯ Time Created: **" + member.getTimeCreated().toLocalDate()
                        , true)
                .setTimestamp(OffsetDateTime.now())
                .build();
        return embed;
    }

    public String flagBuilder(Member member) {
        String flagsFinal = "";
        String userFlags = member.getUser().getFlags().toString();

        if(userFlags.contains("DISCORD_EMPLOYEE")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("DISCORD_PARTNER")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("BUGHUNTER_LEVEL_1")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("BUGHUNTER_LEVEL_2")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("HYPESQUAD_EVENTS")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("HOUSE_BRAVERY")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("HOUSE_BRILLIANCE")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("HYPESQUAD_BALANCE")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("EARLY_SUPPORTER")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("TEAM_USER")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("SYSTEM")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("VERIFIED_BOT")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("VERIFIED_DEVELOPER")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("CERTIFIED_MODERATOR")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("PARTNER")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }
        if(userFlags.contains("STAFF")) {
            flagsFinal = flagsFinal + ", <:name:748954989296091187>";
        }

            return flagsFinal.substring(2);
    }
}
