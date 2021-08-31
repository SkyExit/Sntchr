package commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

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
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if(event.getArgs().isEmpty()) {
            event.reply("Please provide an url or a song name!");
            return;
        }

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

        String link = String.join(" ", event.getArgs());

        if(!isURL(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link);
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
