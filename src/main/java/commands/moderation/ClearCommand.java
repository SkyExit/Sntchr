package commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends Command {
    public ClearCommand() {
        this.name = "clear";
        this.help = "clears some messages";
        this.category = new Category("Moderation");
    }

    @Override
    protected void execute(CommandEvent event) {
        OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);

        if (event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            event.getMessage().delete().queue();
            String args = event.getArgs();
            if (!args.isEmpty()) {
                if (Integer.parseInt(args) < 100 && Integer.parseInt(args) > 1) {
                    int values = Integer.parseInt(args);
                    event.getMessage().delete().queue();
                    List<Message> messages = event.getChannel().getHistory().retrievePast(values).complete();
                    event.getTextChannel().deleteMessages(messages).queue();
                    event.getChannel().sendMessage("✅ " + args.toString() + " messages deleted!").queue(m ->
                            m.delete().queueAfter(5, TimeUnit.SECONDS));
                } else {
                    event.reply("There's an maximum of 100 Messages to clear at once");
                }
            } else {
                event.reply("Please provide an amount of messages to clear!");
            }
        }
    }
}