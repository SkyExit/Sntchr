package de.laurinhummel.sntchr.events;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StatusManager extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("/help"));
    }
}
