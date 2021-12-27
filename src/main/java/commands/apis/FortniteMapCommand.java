package commands.apis;

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
import java.util.Objects;
import java.util.Scanner;

public class FortniteMapCommand extends Command {
    public FortniteMapCommand() {
        this.name = "fnmap";
        this.help = "Shows the Fortnite map!";
        this.category = new Category("Fortnite");
    }

    @Override
    protected void execute(CommandEvent event) {
        //API REQUESTER
        String[] args = event.getArgs().split(" ");
        Response response = null;
        int responsecode = 0;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            URL url = new URL("https://fortnite-api.com/v1/map?language=de");
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
        if(args.length == 1) {
            if(Objects.equals(args[0], "blank")) {
                assert jsonObject != null;
                MessageEmbed embed = new EmbedBuilder()
                        .setTitle("The Fortnite Map")
                        .setColor(Color.RED)
                        .setTimestamp(OffsetDateTime.now())
                        .setFooter("Requested by", event.getAuthor().getAvatarUrl())
                        .setImage(jsonObject.getJSONObject("data").getJSONObject("images").getString("blank"))
                        .build();
                event.reply(embed);
                return;
            }
        }

        assert jsonObject != null;
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("The Fortnite Map")
                .setColor(Color.RED)
                .setTimestamp(OffsetDateTime.now())
                .setFooter("use 's&fnmap blank' for blank map!", event.getAuthor().getAvatarUrl())
                .setImage(jsonObject.getJSONObject("data").getJSONObject("images").getString("pois"))
                .build();
        event.reply(embed);
    }
}
