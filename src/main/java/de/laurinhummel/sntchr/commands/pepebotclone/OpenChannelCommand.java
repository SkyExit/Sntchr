package de.laurinhummel.sntchr.commands.pepebotclone;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;

import java.util.EnumSet;

public class OpenChannelCommand extends SlashCommand {
    public OpenChannelCommand() {
        this.name = "vc-open";
        this.help = "Opens the channel for public";
        this.category = new Category("VoiceChannel");
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member member = event.getMember();
        try {
            if(!member.getVoiceState().inAudioChannel()) { event.reply("You are not in an voice channel").setEphemeral(true).queue(); return; }

            AudioChannelUnion audioChannelUnion = member.getVoiceState().getChannel();
            AudioChannelManager<?, ?> manager = audioChannelUnion.getManager();
                manager.putPermissionOverride(event.getGuild().getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), null);
                manager.putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null);

            manager.queue();
            event.reply("Channel '" + audioChannelUnion.getName() + "' was opened").setEphemeral(true).queue();
        } catch (NullPointerException ex) {
            System.out.println("Something went wrong in OpenChannelCommand");
            event.reply("Something went wrong").setEphemeral(true).queue();
        }

    }
}
