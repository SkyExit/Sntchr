package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkipCommand extends Command {
    public SkipCommand() {
        this.name = "skip";
        this.help = "Skips the current Song!";
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

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        assert memberVoiceState != null;
        if(!memberVoiceState.inVoiceChannel()) {
            event.reply("You need to be in a voice channel!");
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You need to be in the same voice channel as me!");
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if(audioPlayer.getPlayingTrack() == null) {
            event.reply("There is no track playing");
            return;
        }

        musicManager.scheduler.nextTrack();
        event.reply("Skipped this song!");
    }
}
