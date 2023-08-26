package de.laurinhummel.sntchr.commands.economy;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.laurinhummel.sntchr.Sntchr;
import de.laurinhummel.sntchr.shortcuts.CommonStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import de.laurinhummel.sntchr.shortcuts.DataHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class SlotCommand extends SlashCommand {
    public SlotCommand() {
        this.name = "slot";
        this.help = "Slot!";
        this.category = new Category("Economy");

        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "amount", "The amount of coins you want to play with!").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int amount = (int) event.getOption("amount").getAsDouble();
            if(amount < 0) { event.reply(CommonStrings.AMOUNT_0).setEphemeral(true).queue(); return; }

        if(DataHandler.changeUserBalance(event.getMember(), -amount)) {
            int v1 = Sntchr.getRandomNumber(1, 7);
            int v2 = Sntchr.getRandomNumber(1, 7);
            int v3 = Sntchr.getRandomNumber(1, 7);

            boolean win = (v1 == v2) && (v1 == v3);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("SLOT MACHINE")
                    .setDescription(win ? "You won **" + amount*500 + "$!!!**" : "You loose **" + amount + "$** :(")
                    .addField("SLOT", getLetters(v1), true)
                    .addField("SLOT", getLetters(v2), true)
                    .addField("SLOT", getLetters(v3), true);
            event.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
            if(win) { DataHandler.changeUserBalance(event.getMember(), amount * 500); }
        } else {
            event.reply(CommonStrings.NO_MONEY).setEphemeral(true).queue();
        }
    }

    private String getLetters(int letters) {
        String let = switch (letters) {
            case 1 ->   "╭━━━╮\n" +
                        "┃╭━╮┃\n" +
                        "┃┃╱┃┃\n" +
                        "┃╰━╯┃\n" +
                        "┃╭━╮┃\n" +
                        "╰╯//╰╯";

            case 2 ->   "╭━━╮\n" +
                        "┃╭╮┃\n" +
                        "┃╰╯╰╮\n" +
                        "┃╭━╮┃\n" +
                        "┃╰━╯┃\n" +
                        "╰━━━╯";
            case 3 ->   "╭━━━╮\n" +
                        "┃╭━╮┃\n" +
                        "┃┃╱╰╯\n" +
                        "┃┃╱╭╮\n" +
                        "┃╰━╯┃\n" +
                        "╰━━━╯";
            case 4 ->   "╭━━━╮\n" +
                        "╰╮╭╮┃\n" +
                        "╱┃┃┃┃\n" +
                        "╱┃┃┃┃\n" +
                        "╭╯╰╯┃\n" +
                        "╰━━━╯";
            case 5 ->   "╭━━━╮\n" +
                        "┃╭━━╯\n" +
                        "┃╰━━╮\n" +
                        "┃╭━━╯\n" +
                        "┃╰━━╮\n" +
                        "╰━━━╯";
            case 6 ->   "╭━━━╮\n" +
                        "┃╭━━╯\n" +
                        "┃╰━━╮\n" +
                        "┃╭━━╯\n" +
                        "┃┃\n" +
                        "╰╯";
            case 7 ->   "╭━━━╮\n" +
                        "┃╭━╮┃\n" +
                        "┃┃╱╰╯\n" +
                        "┃┃╭━╮\n" +
                        "┃╰┻━┃\n" +
                        "╰━━━╯";
            default -> throw new IllegalStateException("Unexpected value: " + letters);
        };

        return let;
    }
}
