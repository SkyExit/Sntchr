package de.laurinhummel.sntchr.commands.pepebotclone;

import de.laurinhummel.sntchr.Sntchr;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class CreateChannelJoinEvent extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if(event.getVoiceState().inAudioChannel()) {
            // HE JOINED A VC
            AudioChannelUnion vc = event.getChannelJoined();
            if(event.getChannelLeft() != null) { createChannelLeaveAction(event); }
            if(vc.getName().toLowerCase().contains("create channel")) { createChannelJoinAction(event); }
        } else {
            // HE LEFT A VC
            createChannelLeaveAction(event);
        }
    }

    private void createChannelJoinAction(GuildVoiceUpdateEvent event) {
        //System.out.println("user joined 'create channel'");
        try {
            int randomID = Sntchr.getRandomNumber(1, 10);
            String channelName = event.getMember().getEffectiveName() + "'s VC - Sntchr/" + randomID;
            Category category = event.getChannelJoined().getParentCategory();
            assert category != null;

            category.createVoiceChannel(channelName)
                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                    .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .setBitrate(96000)
                    .queue();

            TimeUnit.SECONDS.sleep(1);

            event.getGuild().moveVoiceMember(event.getMember(), event.getGuild().
                    getVoiceChannelsByName(channelName, true).get(0)).queue();
        } catch (NullPointerException ex) { System.out.println("Something went wrong with create channel join event");
        } catch (InterruptedException e) { throw new RuntimeException(e); }

    }

    private void createChannelLeaveAction(GuildVoiceUpdateEvent event) {
        //System.out.println("user left a channel");
        AudioChannelUnion vc = event.getChannelLeft();
        if(vc.getName().contains("VC - Sntchr")) {
            if(vc.getMembers().isEmpty()) { vc.delete().queueAfter(1, TimeUnit.SECONDS); }
        }
    }
}
