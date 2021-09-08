package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackURL, Member member) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack playingTrack) {
                musicManager.scheduler.queue(playingTrack);

                final AudioTrackInfo trackInfo = playingTrack.getInfo();
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle(trackInfo.title, trackInfo.uri)
                        .setDescription(        "```" + TimeUnit.MILLISECONDS.toMinutes(playingTrack.getPosition()) + "m " +
                                (TimeUnit.MILLISECONDS.toSeconds(playingTrack.getPosition()) - (TimeUnit.MILLISECONDS.toMinutes(playingTrack.getPosition()) * 60)) + "s "
                                + "/ " +
                                TimeUnit.MILLISECONDS.toMinutes(playingTrack.getDuration()) + "m " +
                                (TimeUnit.MILLISECONDS.toSeconds(playingTrack.getDuration()) - (TimeUnit.MILLISECONDS.toMinutes(playingTrack.getDuration()) * 60)) + "s " +
                                "```")
                        //.setDescription("```" + playingTrack.getPosition() + "/" + playingTrack.getDuration() + "```")
                        .setColor(new Color(59, 178, 237))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter("Requested by " + member.getUser().getName(), null)
                        .setThumbnail("https://img.youtube.com/vi/" + playingTrack.getIdentifier() + "/default.jpg")
                        .setAuthor("Added to Queue", null, member.getUser().getAvatarUrl())
                        .addField("Interpret", trackInfo.author, true);
                channel.sendMessage(builder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                final AudioTrack playingTrack = tracks.get(0);
                musicManager.scheduler.queue(tracks.get(0));
                final AudioTrackInfo trackInfo = tracks.get(0).getInfo();
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle(trackInfo.title, trackInfo.uri)
                        .setDescription(        "```" + TimeUnit.MILLISECONDS.toMinutes(playingTrack.getPosition()) + "m " +
                                (TimeUnit.MILLISECONDS.toSeconds(playingTrack.getPosition()) - (TimeUnit.MILLISECONDS.toMinutes(playingTrack.getPosition()) * 60)) + "s "
                                + "/ " +
                                TimeUnit.MILLISECONDS.toMinutes(playingTrack.getDuration()) + "m " +
                                (TimeUnit.MILLISECONDS.toSeconds(playingTrack.getDuration()) - (TimeUnit.MILLISECONDS.toMinutes(playingTrack.getDuration()) * 60)) + "s " +
                                "```")
                        //.setDescription("```" + playingTrack.getPosition() + "/" + playingTrack.getDuration() + "```")
                        .setColor(new Color(59, 178, 237))
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter("Requested by " + member.getUser().getName(), null)
                        .setThumbnail("https://img.youtube.com/vi/" + playingTrack.getIdentifier() + "/default.jpg")
                        .setAuthor("Added to Queue", null, member.getUser().getAvatarUrl())
                        .addField("Interpret", trackInfo.author, true);
                channel.sendMessage(builder.build()).queue();
            }

            /*
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                channel.sendMessage("Adding to Queue: `")
                        .append(String.valueOf(tracks.size()))
                        .append("`  Tracks from playlist  `")
                        .append(audioPlaylist.getName())
                        .append("`")
                        .queue();

                for (final AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }
            }

             */


            @Override
            public void noMatches() {
                channel.sendMessage("No Matches").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage("Load failed").queue();
                e.printStackTrace();
            }
        });
    }

    public static PlayerManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
