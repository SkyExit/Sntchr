package de.laurinhummel.sntchr.commands.apis;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Scanner;

public class FortniteNewsCommand extends Command {
    public FortniteNewsCommand() {
        this.name = "fnnews";
        this.help = "Shows the Fortnite news!";
        this.category = new Category("Fortnite");
    }

    @Override
    protected void execute(CommandEvent event) {
        //API REQUESTER
        Response response = null;
        int responsecode = 0;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            URL url = new URL("https://fortnite-api.com/v2/news?language=de");
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
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(response.body().charStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();

                jsonObject = new JSONObject(inline);
                //System.out.println(inline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //DATA WORKER
        assert jsonObject != null;
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("What's up in Fortnite?")
                .setColor(Color.CYAN)
                .setTimestamp(OffsetDateTime.now())
                .setFooter("Requested by", event.getAuthor().getAvatarUrl())
                .setImage(jsonObject.getJSONObject("data").getJSONObject("br").getString("image"))
                .build();
        event.reply(embed);
    }
}
