package commands.information;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class AvatarCommand extends SlashCommand {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[]{"pp", "pb"};
        this.help = "Returns the mentioned users Avatar";
        this.category = new Category("Information");

        this.options = Collections.singletonList(new OptionData(OptionType.USER, "member", "The member you want the avatar from").setRequired(false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(event.getOption("member") == null) {
            Member member = event.getMember();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(10189801))
                    .setTimestamp(event.getTimeCreated())
                    .setImage(member.getUser().getAvatarUrl() + "?size=512")
                    .setAuthor(member.getUser().getName() + "'s Avatar", member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                    .build();
            event.replyEmbeds(embed).queue();
        } else {
            Member member = event.getOption("member").getAsMember();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(10189801))
                    .setTimestamp(event.getTimeCreated())
                    .setImage(member.getUser().getAvatarUrl() + "?size=512")
                    .setAuthor(member.getUser().getName() + "'s Avatar", member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                    .build();
            event.replyEmbeds(embed).queue();
        }
    }
}