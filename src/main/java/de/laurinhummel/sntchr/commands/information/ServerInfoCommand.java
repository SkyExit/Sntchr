package de.laurinhummel.sntchr.commands.information;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.OffsetDateTime;

public class ServerInfoCommand extends SlashCommand {
    public ServerInfoCommand() {
        this.name = "serverinfo";
        this.help = "Informations about the Server!";
        this.category = new Category("Information");
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription("**Guild information for " + event.getGuild().getName() + "**")
                .setColor(event.getGuild().getSelfMember().getColor())
                .setThumbnail(event.getGuild().getIconUrl())
                .addField("General",
                        "**❯ Name: **" + event.getGuild().getName()  +" \n" +
                                "**❯ ID: **" + event.getGuild().getId()  +" \n" +
                                "**❯ Owner: **" + event.getGuild().getOwner().getUser().getAsTag()  +" \n" +
                                "**❯ Region: **" + event.getGuild().getRegion()  +" \n" +
                                "**❯ Time Created: **" + event.getGuild().getTimeCreated().toLocalDate()
                        , true)
                .addField("Statistics",
                        "**❯ Role Count: ** " + event.getGuild().getRoles().size() + "\n" +
                                "**❯ Emoji Count: ** " + event.getGuild().getEmotes().size() + "\n" +
                                "**❯ Member Count: ** " + event.getGuild().getMembers().size() + "\n" +
                                "**❯ Channel Count: ** " + event.getGuild().getTextChannels().size() + " (TC)" + "\n" +
                                "**❯ Channel Count: ** " + event.getGuild().getVoiceChannels().size() + " (VC)" + "\n" +
                                "**❯ Boost Count: ** " + event.getGuild().getBoostCount() + "\n"
                        , true)
                .setTimestamp(OffsetDateTime.now())
                .build();
        event.replyEmbeds(embed).queue();
    }
}
