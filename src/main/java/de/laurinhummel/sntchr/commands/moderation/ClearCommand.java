package de.laurinhummel.sntchr.commands.moderation;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClearCommand extends SlashCommand {
    public ClearCommand() {
        this.name = "clear";
        this.help = "clears some messages";
        this.category = new Category("Moderation");

        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "amount", "The amount of messages you want to purge!").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (event.getMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            int amount = (int) event.getOption("amount").getAsDouble();
                if (amount <= 100 && amount > 1) {
                    List<Message> messages = event.getChannel().getHistory().retrievePast(amount).complete();
                    try {
                        event.getTextChannel().deleteMessages(messages).queue();
                        event.getChannel().sendMessage("✅ " + amount + " messages deleted!").queue(m ->
                                m.delete().queueAfter(5, TimeUnit.SECONDS));
                    } catch (IllegalArgumentException e) {
                        event.reply("I cant delete messages older than 2 weeks... sry").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("You can't clear more than 100 messages at a time!").setEphemeral(true).queue();
                }
        } else {
            event.reply("You don't have permissions to do that!").setEphemeral(true).queue();
        }
    }
}