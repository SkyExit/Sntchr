package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class VolumeCommand extends Command {
    public VolumeCommand() {
        this.name = "volume";
        this.help = "Changes the Volume";
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

        if(event.getArgs().isEmpty()) {
            event.reply("Please provide the new Volume!");
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        try {
            int vol = Integer.parseInt(event.getArgs());
            if(vol > 1) {
                musicManager.audioPlayer.setVolume(vol);
                event.reply("The volume has been set to **" + vol + "**");
            } else {
                event.reply("Please provide a volume > 1");
                return;
            }
        } catch (NumberFormatException e) {
            event.reply("Unable to parse given arguments");
            return;
        }
    }
}
