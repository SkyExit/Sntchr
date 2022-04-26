package de.laurinhummel.sntchr.commands.economy;

import com.jagrosh.jdautilities.command.SlashCommand;
import de.laurinhummel.sntchr.commands.apis.ValorantWeaponStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import de.laurinhummel.sntchr.shortcuts.DataHandler;
import net.dv8tion.jda.api.utils.data.DataType;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;

public class BalanceCommand extends SlashCommand {
    public BalanceCommand() {
        this.name = "balance";
        this.help = "Returns the mentioned users Balance";
        this.category = new Category("Economy");

        this.children = new SlashCommand[]{new Get(), new Set()};
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }

    private static class Get extends SlashCommand {
        public Get() {
            this.name = "get";
            this.help = "Gets a users balance";

            this.options = Collections.singletonList(new OptionData(OptionType.USER, "user", "The user you the balance from").setRequired(false));
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(false).queue();
            if(event.getOption("member") == null) {
                event.getHook().sendMessageEmbeds(balanceEmbedBuilder(event.getMember(), event.getTimeCreated())).queue();
                //event.replyEmbeds(balanceEmbedBuilder(event.getMember(), event.getTimeCreated())).queue();
            } else {
                //event.replyEmbeds(balanceEmbedBuilder(event.getOption("member").getAsMember(), event.getTimeCreated())).queue();
                event.getHook().sendMessageEmbeds(balanceEmbedBuilder(event.getOption("user").getAsMember(), event.getTimeCreated())).queue();
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

    private static class Set extends SlashCommand {
        public Set() {
            this.name = "set";
            this.help = "Sets a users balance";

            ArrayList<OptionData> options = new ArrayList<>();
                options.add((new OptionData(OptionType.USER, "user", "The user you want to change the balance").setRequired(true)));
                options.add(new OptionData(OptionType.INTEGER, "amount", "The amount of changing").setRequired(true));
                this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                Member member = event.getOption("user").getAsMember();
                int amount = (int) event.getOption("amount").getAsDouble();

                DataHandler.setUserBalance(member, amount);
                event.getHook().sendMessage("Your balance was updated! New value: " + amount).setEphemeral(true).queue();
            } else {
                event.getHook().sendMessage("Sorry, but you don't have permissions to do that :/").setEphemeral(true).queue();
            }
        }
    }
}
