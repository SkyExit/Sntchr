package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand extends Command {
    public QueueCommand() {
        this.name = "queue";
        this.help = "Shows the queue";
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        final TextChannel textChannel = event.getTextChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

        if(queue.isEmpty()) {
            event.reply("The current queue is empty");
            return;
        }

        final int trackCount = Math.min(queue.size(), 20);
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final MessageAction messageAction = event.getTextChannel().sendMessage("**Current Queue:** \n");

        for (int i = 0; i < trackCount; i++) {
            final AudioTrack audioTrack = trackList.get(i);
            final AudioTrackInfo info = audioTrack.getInfo();

            messageAction.append('#')
                    .append(String.valueOf(i + 1))
                    .append(" `")
                    .append(info.title)
                    .append(" by ")
                    .append(info.author)
                    .append("` [`")
                    .append(formatTime(audioTrack.getDuration()))
                    .append("`]\n");
        }

        if(trackList.size() > trackCount) {
            messageAction.append("And `")
                    .append(String.valueOf(trackList.size() - trackCount))
                    .append("` more...");
        }

        messageAction.queue();
    }

    private String formatTime(long duration) {
        return TimeUnit.MILLISECONDS.toMinutes(duration) + "m " +
                (TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) * 60)) + "s";
    }
}
