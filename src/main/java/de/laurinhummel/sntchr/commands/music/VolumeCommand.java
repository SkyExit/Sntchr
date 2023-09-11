package de.laurinhummel.sntchr.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import de.laurinhummel.sntchr.lavaplayer.GuildMusicManager;
import de.laurinhummel.sntchr.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

public class VolumeCommand extends SlashCommand {
    public VolumeCommand() {
        this.name = "volume";
        this.help = "Changes the Volume";
        this.category = new Category("Music");
        this.options = Collections.singletonList(new OptionData(OptionType.INTEGER, "volume", "The new volume").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        final TextChannel channel = event.getTextChannel();
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        assert selfVoiceState != null;
        if(!selfVoiceState.inAudioChannel()) {
            event.reply("I need to be in a voice channel!").queue();
            return;
        }

        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        assert memberVoiceState != null;
        if(!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel!").queue();
            return;
        }

        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You need to be in the same voice channel as me!").queue();
            return;
        }

        if(event.getOption("volume").getAsString().isEmpty()) {
            event.reply("Please provide the new Volume!").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());

        try {
            int vol = event.getOption("volume").getAsInt();
            if(vol > 1) {
                musicManager.audioPlayer.setVolume(vol);
                event.reply("The volume has been set to **" + vol + "**").queue();
            } else {
                event.reply("Please provide a volume > 1").queue();
                return;
            }
        } catch (NumberFormatException e) {
            event.reply("Unable to parse given arguments").queue();
            return;
        }
    }
}
