package de.laurinhummel.sntchr;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import de.laurinhummel.sntchr.commands.apis.*;
import de.laurinhummel.sntchr.commands.information.AvatarCommand;
import de.laurinhummel.sntchr.commands.information.HelpCommand;
import de.laurinhummel.sntchr.commands.information.ServerInfoCommand;
import de.laurinhummel.sntchr.commands.information.UserInfoCommand;
import de.laurinhummel.sntchr.commands.moderation.ClearCommand;
import de.laurinhummel.sntchr.commands.music.*;
import de.laurinhummel.sntchr.commands.economy.BalanceCommand;
import de.laurinhummel.sntchr.commands.economy.SlotCommand;
import de.laurinhummel.sntchr.events.StatusManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.laurinhummel.sntchr.shortcuts.BotToken;

public final class Sntchr {
    private static Sntchr sntchr;

    public static void main(String args[]) throws Exception {
        JDA jda = JDABuilder.createDefault(BotToken.getBotToken())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();

        Logger.getLogger("net.dv8tion").setLevel(Level.INFO);
        Logger.getLogger("com.jagrosh").setLevel(Level.INFO);
        BasicConfigurator.configure();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("s&");
        builder.setOwnerId("881856922410700830"); //Bot ID
        builder.useHelpBuilder(false);
        builder.forceGuildOnly("803317679590473751");

        //Information
        builder.addSlashCommand(new AvatarCommand());
        builder.addSlashCommand(new HelpCommand());
        builder.addSlashCommand(new ServerInfoCommand());
        builder.addSlashCommand(new UserInfoCommand());

        //Music
        builder.addCommand(new JoinCommand());
        builder.addCommand(new PlayCommand());
        builder.addCommand(new StopCommand());
        builder.addCommand(new PauseCommand());
        builder.addCommand(new ResumeCommand());
        builder.addCommand(new SkipCommand());
        builder.addCommand(new VolumeCommand());
        builder.addCommand(new NowPlayingCommand());
        builder.addCommand(new QueueCommand());
        builder.addCommand(new RepeatCommand());
        builder.addCommand(new LeaveCommad());

        //APIs
        builder.addCommand(new FortniteStatsCommand());
        builder.addCommand(new FortniteNewsCommand());
        builder.addCommand(new FortniteMapCommand());
        builder.addCommand(new ClashRoyaleStatsCommand());
        builder.addSlashCommand(new ValorantWeaponStats());

        //Moderation
        builder.addSlashCommand(new ClearCommand());

        //Economy
        builder.addSlashCommand(new SlotCommand());
        builder.addSlashCommand(new BalanceCommand());

        CommandClient client = builder.build();

        jda.addEventListener(client);
        jda.addEventListener(new StatusManager());
    }

    public static Sntchr getSntchr() {
        return sntchr;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random()*(max-min+1)+min);
    }
}
