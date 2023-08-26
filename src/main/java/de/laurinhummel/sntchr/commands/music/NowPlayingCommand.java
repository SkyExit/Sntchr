package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

public class NowPlayingCommand extends Command {
    public NowPlayingCommand() {
        this.name = "nowplaying";
        this.help = "What's playing?";
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        final TextChannel channel = event.getTextChannel();
        final Member self = event.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        assert selfVoiceState != null;
        if(!selfVoiceState.inAudioChannel()) {
            event.reply("I need to be in a voice channel!");
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final AudioTrack playingTrack = audioPlayer.getPlayingTrack();

        if(playingTrack == null) {
            event.reply("There is no track playing");
            return;
        }

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
                .setFooter("Requested by " + event.getMember().getUser().getName(), null)
                .setThumbnail("https://img.youtube.com/vi/" + playingTrack.getIdentifier() + "/default.jpg")
                .setAuthor("Currently Playing", null, event.getMember().getUser().getAvatarUrl())
                .addField("Interpret", trackInfo.author, true);
        event.reply(builder.build());
    }
}
