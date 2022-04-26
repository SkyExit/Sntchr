package de.laurinhummel.sntchr.commands.apis;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import java.util.Scanner;

public class FortniteStatsCommand extends Command {
    public FortniteStatsCommand() {
        this.name = "fnstats";
        this.help = "Shows your Fortnite stats!";
        this.category = new Category("Fortnite");
    }

    @Override
    protected void execute(CommandEvent event) {
        //PARAMETER BUILDER
        String[] args = event.getArgs().split(" ");
        if(args[0].isEmpty()) {
            event.reply("Please use s&fnstats [name] <epic/ps4/xbox> <lifetime/season>");
        } else {
            String accountName = null;
            String accountType = "epic";
            String timeWindow = "lifetime";
            try {
                accountName = args[0];
                accountType = args[1].isEmpty() ? "epic" : switch (args[1]) {
                    case "pc", "epic" -> "epic";
                    case "ps4", "psn" -> "psn";
                    case "xbox", "xbl" -> "xbl";
                    default -> "epic";
                };
                timeWindow = args[2] == null ? "lifetime" : switch (args[2]) {
                    case "lifetime", "all", "ever" -> "lifetime";
                    case "season", "seasonal" -> "season";
                    default -> "lifetime";
                };
            } catch (IndexOutOfBoundsException e) {
                //e.printStackTrace();
            }


            //URL BUILDER
            URL url = null;
            int responsecode = 0;

            try {
                URL baseURL = new URL("https://fortnite-api.com/v2/stats/br/v2?");
                //Append Name
                if(args[0] != null) {
                    String  buildBaseURL = baseURL.toString() + "name=" + args[0].toString() + "&";
                            buildBaseURL = buildBaseURL + "accountType=" + accountType + "&";
                            buildBaseURL = buildBaseURL + "timeWindow=" + timeWindow + "&";
                            buildBaseURL = buildBaseURL + "image=" + "all";

                    url = new URL(buildBaseURL);
                    //System.out.println(buildBaseURL);
                }
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
                    event.reply("Requested Account Stats are privat!");
                    return;
                } else if(responsecode == 404) {
                    event.reply("Requested Account Stats aren't available because the account doesn't exist!");
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
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                        .setFooter("Requested by", event.getAuthor().getAvatarUrl())
                        .setImage(data.getString("image"))
                        .addField("BattlePass Level " + data.getJSONObject("battlePass").getInt("level"), fnbp + bpprogress + "%", false)
                        .build();
                event.reply(embed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
