package commands.information;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;

public class HelpCommand extends Command {
    public HelpCommand() {
        this.name = "help";
        this.help = "Shows the help-menu";
        this.category = new Category("Information");
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(new Color(10189801))
                .setTimestamp(event.getMessage().getTimeCreated())
                .setFooter("Requested by " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                .setAuthor("Help Menu (s&)", null, event.getJDA().getSelfUser().getAvatarUrl())
                .addField("Information", "`help`, `avatar`, `serverinfo`, `userinfo`", true)
                .addField("Music", "`join/connect`, `play`, `stop`, `pause`, `resume`, `skip`, `volume`, `nowplaying`, `queue`, `loop/repeat`", true)
                .addField("Moderation", "`clear`", true)
                .addField("Gaming", "`s&fnstats`, `s&fnnews`, `s&fnmap`, `s&crstats`", true)
                .build();
        event.reply(embed);
    }
}
