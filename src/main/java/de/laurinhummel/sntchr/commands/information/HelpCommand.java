package de.laurinhummel.sntchr.commands.information;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class HelpCommand extends SlashCommand {
    enum Categories {
        DEFAULT,
        INFORMATION,
        MODERATION,
        MUSIC,
        GAMING,
        ECONOMY;
    }
    private static Categories categories;

    public HelpCommand() {
        this.name = "help";
        this.help = "Shows the help-menu";
        this.category = new Category("Information");
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};

        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "category", "Extended help for specific category's")
                .addChoice("information", "1")
                .addChoice("moderation", "2")
                .addChoice("music", "3")
                .addChoice("gaming", "4")
                .addChoice("economy", "5")
                .setRequired(false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            switch (Integer.parseInt(event.getOption("category").getAsString())) {
                case 1 : {

                }
                break;
                default: {
                    MessageEmbed embed = getEmbedBuilder(event, "Sntchr Help Menu")
                            .addField("Information", "`help`, `avatar`, `serverinfo`, `userinfo`", true)
                            .addField("Music", "`join/connect`, `play`, `stop`, `pause`, `resume`, `skip`, `volume`, `nowplaying`, `queue`, `loop/repeat`", true)
                            .addField("Moderation", "`clear`", true)
                            .addField("Gaming", "`s&fnstats`, `s&fnnews`, `s&fnmap`, `s&crstats`", true)
                            .build();
                    event.replyEmbeds(embed).queue();
                }
            }
        } catch (NullPointerException ex) {
            MessageEmbed embed = getEmbedBuilder(event, "Sntchr Help Menu")
                    .addField("Information", "`help`, `avatar`, `serverinfo`, `userinfo`", true)
                    .addField("Music", "`join/connect`, `play`, `stop`, `pause`, `resume`, `skip`, `volume`, `nowplaying`, `queue`, `loop/repeat`", true)
                    .addField("Moderation", "`clear`", true)
                    .addField("Gaming", "`s&fnstats`, `s&fnnews`, `s&fnmap`, `s&crstats`", true)
                    .build();
            event.replyEmbeds(embed).queue();
        }
    }

    private EmbedBuilder getEmbedBuilder(SlashCommandEvent event, String title) {

        return new EmbedBuilder()
                .setColor(10189801)
                .setTimestamp(event.getTimeCreated())
                .setFooter("Requested by " + event.getName(), event.getMember().getAvatarUrl())
                .setAuthor(title, null, event.getJDA().getSelfUser().getAvatarUrl());
    }
}
