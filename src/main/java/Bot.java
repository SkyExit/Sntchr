import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import commands.apis.ClashRoyaleStatsCommand;
import commands.apis.FortniteMapCommand;
import commands.apis.FortniteNewsCommand;
import commands.apis.FortniteStatsCommand;
import commands.information.*;
import commands.moderation.ClearCommand;
import commands.music.*;
import events.StatusManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import shortcuts.BotToken;

public class Bot {

    public static void main(String args[]) throws Exception {
        JDA jda = JDABuilder.createDefault(BotToken.getBotToken())
                .enableCache(CacheFlag.VOICE_STATE)
                .build();

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("s&");
        builder.setOwnerId("881856922410700830"); //Bot ID
        builder.useHelpBuilder(false);
        //builder.setHelpWord(null);


        //Information
        builder.addCommand(new AvatarCommand());
        builder.addCommand(new HelpCommand());
        builder.addCommand(new ServerInfoCommand());
        builder.addCommand(new UserInfoCommand());

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

        //Gaming
        builder.addCommand(new FortniteStatsCommand());
        builder.addCommand(new FortniteNewsCommand());
        builder.addCommand(new FortniteMapCommand());
        builder.addCommand(new ClashRoyaleStatsCommand());

        //Moderation
        builder.addCommand(new ClearCommand());

        CommandClient client = builder.build();
        jda.addEventListener(client);

        jda.addEventListener(new StatusManager());
    }
}
