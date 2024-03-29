package de.laurinhummel.sntchr.commands.pepebotclone;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;

import java.util.EnumSet;

public class CloseChannelCommand extends SlashCommand {
    public CloseChannelCommand() {
        this.name = "vc-close";
        this.help = "Closes the channel for public";
        this.category = new Category("VoiceChannel");
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member member = event.getMember();
        try {
            if(!member.getVoiceState().inAudioChannel()) { event.reply("You are not in an voice channel").setEphemeral(true).queue(); return; }

            AudioChannelUnion audioChannelUnion = member.getVoiceState().getChannel();
            AudioChannelManager<?, ?> manager = audioChannelUnion.getManager();
                manager.putPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL));
                manager.putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null);

            manager.queue();
            event.reply("Channel '" + audioChannelUnion.getName() + "' was closed").setEphemeral(true).queue();
        } catch (NullPointerException ex) {
            System.out.println("Something went wrong in CloseChannelCommand");
            event.reply("Something went wrong").setEphemeral(true).queue();
        }
    }
}
