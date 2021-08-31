package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class AvatarCommand extends Command {
    public AvatarCommand() {
        this.name = "avatar";
        this.aliases = new String[]{"pp", "pb"};
        this.help = "Returns the mentioned users Avatar";
        this.category = new Category("Information");
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            event.reply("Please mention someone!");
        } else {
            Member member = event.getMessage().getMentionedMembers().get(0);
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(new Color(10189801))
                    .setTimestamp(event.getMessage().getTimeCreated())
                    .setImage(member.getUser().getAvatarUrl() + "?size=512")
                    .setAuthor(member.getUser().getName() + "'s Avatar", member.getUser().getAvatarUrl(), member.getUser().getAvatarUrl())
                    .build();
            event.reply(embed);
        }
    }
}