package de.laurinhummel.sntchr.commands.pepebotclone;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;

import java.util.Collections;
import java.util.EnumSet;

public class KickChannelCommand extends SlashCommand {
    public KickChannelCommand() {
        this.name = "vc-kick";
        this.help = "Kicks someone from the channel";
        this.category = new Command.Category("VoiceChannel");
        this.options = Collections.singletonList(new OptionData(OptionType.USER, "member", "The member you to kick").setRequired(true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member owner = event.getMember();
        Member kick = event.getOption("member").getAsMember();

        try {
            if(!owner.getVoiceState().inAudioChannel()) { event.reply("You are not in an voice channel").setEphemeral(true).queue(); return; };

            AudioChannelUnion audioChannelUnion = owner.getVoiceState().getChannel();
            AudioChannelManager<?, ?> manager = audioChannelUnion.getManager();
                manager.putPermissionOverride(kick, null, EnumSet.of(Permission.VIEW_CHANNEL));

            manager.queue();
            event.reply(kick.getEffectiveName() + " was invited to join '" + audioChannelUnion.getName() + "'").setEphemeral(true).queue();
        } catch (NullPointerException ex) {
            System.out.println("Something went wrong in CloseChannelCommand");
            event.reply("Something went wrong").setEphemeral(true).queue();
        }
    }
}
