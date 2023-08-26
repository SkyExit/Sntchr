package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;

public class PlayCommand extends Command {
        public PlayCommand() {
        this.name = "play";
        this.help = "Plays a Song";
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        final TextChannel channel = event.getTextChannel();
        final Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if(event.getArgs().isEmpty()) {
            event.reply("Please provide an url or a song name!");
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        assert memberVoiceState != null;
        if(!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel!");
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final AudioChannelUnion memberVoiceChannel = memberVoiceState.getChannel();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        assert selfVoiceState != null;
        if(!selfVoiceState.inAudioChannel()) {
            if(self.hasPermission(Permission.VOICE_CONNECT)) {
                musicManager.audioPlayer.setVolume(100);
                audioManager.openAudioConnection(memberVoiceChannel);
                assert memberVoiceChannel != null;
                event.reply("Connecting to " + memberVoiceChannel.getName());
                selfVoiceState = memberVoiceState;
            } else {
                event.reply("I can't connect to the channel because i don't have permissions");
            }
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You need to be in the same voice channel as me!");
            return;
        }

        String link = String.join(" ", event.getArgs());

        if(!isURL(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link, event.getMember());
    }

    private boolean isURL(String link) {
            try {
                new URI(link);
                return true;
            } catch (URISyntaxException e) {
                return false;
            }
    }
}
