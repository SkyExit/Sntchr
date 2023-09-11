package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends SlashCommand {
    public QueueCommand() {
        this.name = "queue";
        this.help = "Shows the queue";
        this.category = new Category("Music");
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        final TextChannel textChannel = event.getTextChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if(queue.isEmpty()) {
            event.reply("The current queue is empty").queue();
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final MessageCreateAction messageAction = event.getTextChannel().sendMessage("**Current Queue:**");
            messageAction.addContent("  \n");

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack audioTrack = trackList.get(i);
            final AudioTrackInfo info = audioTrack.getInfo();

            messageAction.addContent("#")
                    .addContent(String.valueOf(i + 1))
                    .addContent(" `")
                    .addContent(info.title)
                    .addContent(" by ")
                    .addContent(info.author)
                    .addContent("` [`")
                    .addContent(formatTime(audioTrack.getDuration()))
                    .addContent("`]\n");
        }

        if(trackList.size() > trackCount) {
            messageAction.addContent("And `")
                    .addContent(String.valueOf(trackList.size() - trackCount))
                    .addContent("` more...");
        }

        //messageAction.queue();
        event.reply(messageAction.getContent()).queue();
    }

    private String formatTime(long duration) {
        return TimeUnit.MILLISECONDS.toMinutes(duration) + "m " +
                (TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) * 60)) + "s";
    }
}
