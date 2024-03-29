package de.laurinhummel.sntchr.commands.apis;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

public class FortniteStatsCommand extends SlashCommand {
    public FortniteStatsCommand() {
        this.name = "fnstats";
        this.help = "Shows your Fortnite stats!";
        this.category = new Category("Fortnite");

        List<OptionData> s = new ArrayList<>();
                s.add(new OptionData(OptionType.STRING, "name", "The username you want to track the stats").setRequired(true));
                s.add(new OptionData(OptionType.STRING, "platform", "The platform the account was created on").setRequired(true)
                        .addChoice("epic/pc", "epic")
                        .addChoice("ps4/psn", "psn")
                        .addChoice("xbox", "xbl"));
                s.add(new OptionData(OptionType.STRING, "period", "The time window you want your stats for").setRequired(true)
                        .addChoice("lifetime", "lifetime")
                        .addChoice("season", "season"));

        this.options = s;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        //PARAMETER BUILDER
        String accountName = event.getOption("name").getAsString();
        String accountType = event.getOption("platform").getAsString();
        String timeWindow = event.getOption("period").getAsString();

        //URL BUILDER
        URL url = null;
        int responsecode = 0;

        try {
            URL baseURL = new URL("https://fortnite-api.com/v2/stats/br/v2?");
            //Append Name
            StringBuilder stringBuilder = new StringBuilder()
                    .append(baseURL + "name=" + accountName + "&")
                    .append("accountType=" + accountType + "&")
                    .append("timeWindow=" + timeWindow + "&")
                    .append("image=" + "all");

                url = new URL(stringBuilder.toString());
                //System.out.println(stringBuilder.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        //API REQUESTER
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            assert url != null;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "006fcf81-1dfa-4466-8b52-658ec362b570")
                    .build();

            Call call = client.newCall(request);
            response = call.execute();
            responsecode = response.code();

        } catch(IOException e) {
            e.printStackTrace();
        }

        //DATA MINER
        JSONObject jsonObject = null;
        try {
            if (responsecode == 403) {
                event.reply("Requested Account Stats are privat!").setEphemeral(true).queue();
                return;
            } else if(responsecode == 404) {
                event.reply("Requested Account Stats aren't available because the account doesn't exist!").setEphemeral(true).queue();
                return;
            } else if(responsecode == 200) {

                String inline = "";
                Scanner scanner = new Scanner(response.body().charStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();

                jsonObject = new JSONObject(inline);
                //System.out.println(inline);
            } else {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            }
        } catch (Exception e) { e.printStackTrace(); }
        response.close();

        //DATA WORKER
        try {
            JSONObject data = jsonObject.getJSONObject("data");

            int bpprogress = data.getJSONObject("battlePass").getInt("progress");
            int barlvl = Math.round((float) bpprogress/(100/30));
            String ch1 = "▒";
            String ch2 = "░";
            String fnbp = "[" + ch1.repeat(barlvl) + ch2.repeat(30-barlvl) + "] ";

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Player Stats for " + data.getJSONObject("account").getString("name"))
                    .setColor(Color.ORANGE)
                    .setTimestamp(OffsetDateTime.now())
                    .setFooter("Requested by", event.getMember().getAvatarUrl())
                    .setImage(data.getString("image"))
                    .addField("BattlePass Level " + data.getJSONObject("battlePass").getInt("level"), fnbp + bpprogress + "%", false)
                    .build();
            event.replyEmbeds(embed).setEphemeral(false).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

