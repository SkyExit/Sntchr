package economy;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import shortcuts.DataHandler;

public class SlotCommand extends SlashCommand {
    public SlotCommand() {
        this.name = "slot";
        this.help = "Slot!";
        this.category = new Category("Economy");
    }
    @
            Override
    protected void execute(SlashCommandEvent event) {
        int v = DataHandler.getRandomNumber(-5000, 5);
        event.reply(DataHandler.getUserBalance(event.getMember()) + "; added: " + v + "; "
                + DataHandler.changeUserBalance(event.getMember(), v) + "; new: " + DataHandler.getUserBalance(event.getMember())).setEphemeral(true).queue();
    }
}
