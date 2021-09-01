package commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.time.OffsetDateTime;

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
        if(!selfVoiceState.inVoiceChannel()) {
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
                .setDescription("```" + playingTrack.getPosition() + "/" + playingTrack.getDuration() + "```")
                .setColor(new Color(59, 178, 237))
                .setTimestamp(OffsetDateTime.now())
                .setFooter("Requested by " + event.getMember().getUser().getName(), null)
                .setThumbnail(trackInfo.uri)
                .setAuthor("Added to Queue", null, event.getMember().getUser().getAvatarUrl())
                .addField("Interpret", trackInfo.author, true)
                .addField("Views", "these last two", true);
    }
}
