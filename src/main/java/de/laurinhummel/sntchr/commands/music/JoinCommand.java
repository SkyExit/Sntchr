package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command {
    public JoinCommand() {
        this.name = "join";
        this.help = "Join's the channel";
        this.category = new Category("Music");
        this.aliases = new String[]{"connect"};
    }

    @Override
    protected void execute(CommandEvent event) {
        final TextChannel textChannel = event.getTextChannel();
        final Member self = event.getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();
        assert selfVoiceState != null;
        if(selfVoiceState.inAudioChannel()) {
            event.reply("I'm already in a voice channel!");
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        assert memberVoiceState != null;
        if(!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel first!");
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final AudioChannelUnion memberVoiceChannel = memberVoiceState.getChannel();

        if(self.hasPermission(Permission.VOICE_CONNECT)) {
            musicManager.audioPlayer.setVolume(100);
            audioManager.openAudioConnection(memberVoiceChannel);
            assert memberVoiceChannel != null;
            event.reply("Connecting to " + memberVoiceChannel.getName());
        } else {
            event.reply("I can't connect to the channel because i don't have permissions");
        }

    }
}
