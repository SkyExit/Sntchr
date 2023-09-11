package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends SlashCommand {
    public JoinCommand() {
        this.name = "join";
        this.help = "Join's the channel";
        this.category = new Category("Music");
        this.aliases = new String[]{"connect"};
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        final TextChannel textChannel = event.getTextChannel();
        final Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();
        assert selfVoiceState != null;
        if(selfVoiceState.inAudioChannel()) {
            event.reply("I'm already in a voice channel!").queue();
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        assert memberVoiceState != null;
        if(!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel first!").queue();
            return;
        }

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final AudioChannelUnion memberVoiceChannel = memberVoiceState.getChannel();

        if(self.hasPermission(Permission.VOICE_CONNECT)) {
            musicManager.audioPlayer.setVolume(100);
            audioManager.openAudioConnection(memberVoiceChannel);
            assert memberVoiceChannel != null;
            event.reply("Connecting to " + memberVoiceChannel.getName()).queue();
        } else {
            event.reply("I can't connect to the channel because i don't have permissions").queue();
        }

    }
}
