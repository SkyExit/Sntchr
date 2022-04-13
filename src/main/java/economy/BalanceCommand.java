package economy;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.Timestamp;
import shortcuts.DataHandler;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;

public class BalanceCommand extends SlashCommand {
    public BalanceCommand() {
        this.name = "balance";
        this.aliases = new String[]{"bal", "eco"};
        this.help = "Returns the mentioned users Balance";
        this.category = new Category("Economy");

        this.options = Collections.singletonList(new OptionData(OptionType.USER, "member", "The member you want the avatar from").setRequired(false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(event.getOption("member") == null) {
            event.replyEmbeds(balanceEmbedBuilder(event.getMember(), event.getTimeCreated())).queue();
        } else {
            event.replyEmbeds(balanceEmbedBuilder(event.getOption("member").getAsMember(), event.getTimeCreated())).queue();
        }
    }

    private MessageEmbed balanceEmbedBuilder(Member member, TemporalAccessor timestamp) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(10189801))
                .setTimestamp(timestamp)
                .setThumbnail(member.getUser().getAvatarUrl() + "?size=128")
                .setAuthor(member.getUser().getName() + "'s Balance", member.getUser().getAvatarUrl())
                .addField("BALANCE", String.valueOf(DataHandler.getUserBalance(member)), false)
                .build();

        return embed;
    }
}
