package commands.apis;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Scanner;

public class ClashRoyaleStatsCommand extends Command {
    public ClashRoyaleStatsCommand() {
        this.name = "crstats";
        this.help = "Shows your CR stats!";
        this.category = new Category("CR");
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        String bearer = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjcxYmEwYzViLTM1NTEtNGFkOC1hY2Y4LTdjNmEzMzQwZDU0MyIsImlhdCI6MTY0MDYxOTg4Miwic3ViIjoiZGV2ZWxvcGVyLzg5OGQxYTg3LWI4ZDgtOTE5Yi0zYmMyLTU2OGVhYTYwNjVmNyIsInNjb3BlcyI6WyJyb3lhbGUiXSwibGltaXRzIjpbeyJ0aWVyIjoiZGV2ZWxvcGVyL3NpbHZlciIsInR5cGUiOiJ0aHJvdHRsaW5nIn0seyJjaWRycyI6WyIxMzUuMTI1LjIzMy4xMTQiXSwidHlwZSI6ImNsaWVudCJ9XX0.y_fXMEHlBEi5YB6tbP5X9WJUbj-wDehYrSlLW_ei56CkNgxTqf6qU4qL-6h4jat10ev9KHfD-vTFAKNzBXcQ6g";

        if(args[0].isEmpty() || args.length > 1) {
            event.reply("Please use s&crstats #yourTag");
            return;
        }

        //API REQUESTER
        Response response = null;
        int responsecode = 0;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            URL url = null;
            if(args[0].contains("#") && args[0].charAt(0) == '#') {
                url = new URL("https://api.clashroyale.com/v1/players/%23" + args[0].substring(1));
            } else {
                url = new URL("https://api.clashroyale.com/v1/players/%23" + args[0]);
            }

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer)
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


        //2nd CLAN API REQUESTER
        Response response2 = null;
        int responsecode2 = 0;
        try {
            OkHttpClient client2 = new OkHttpClient.Builder()
                    .build();
            String clanTag = jsonObject.getJSONObject("clan").getString("tag").substring(1);
            URL url2 = null;
            url2 = new URL("https://api.clashroyale.com/v1/clans/%23" + clanTag);
            Request request = new Request.Builder()
                    .url(url2)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer)
                    .build();
            Call call2 = client2.newCall(request);
            response2 = call2.execute();
            responsecode2 = response2.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //2nd DATA MINER
        JSONObject jsonObject2 = null;
        try {
            if (responsecode2 != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode2);
            } else {

                String inline2 = "";
                Scanner scanner2 = new Scanner(response2.body().charStream());
                while (scanner2.hasNext()) {
                    inline2 += scanner2.nextLine();
                }
                scanner2.close();

                jsonObject2 = new JSONObject(inline2);
                //System.out.println(inline2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //DATA WORKER
        try {
            //EXP BAR
            int cronelvl = jsonObject.getInt("expLevel");
            float lvlprogress = jsonObject.getInt("expPoints");
            float reqEXP = switch (cronelvl) {
                case 1 -> 20;
                case 2 -> 50;
                case 3 -> 100;
                case 4 -> 200;
                case 5 -> 400;
                case 6 -> 1000;
                case 7 -> 2000;
                case 8 -> 5000;
                case 9 -> 10000;
                case 10 -> 20000;
                case 11 -> 35000;
                case 12 -> 50000;
                case 13 -> 80000;
                default -> 0;
            };
            int lvlprogresspercentage = Math.round((float) (lvlprogress/reqEXP)*100);
            int barlvl = Math.round((float) lvlprogresspercentage/(100/30));
            String ch1 = "▒";
            String ch2 = "░";
            String crlvlpb = "[" + ch1.repeat(barlvl) + ch2.repeat(30-barlvl) + "] ";

            assert jsonObject2 != null;
            JSONArray currentDeck = jsonObject.getJSONArray("currentDeck");
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Player Stats for " + jsonObject.getString("name") + " (" + jsonObject.getString("tag") + ")")
                    .setColor(Color.ORANGE)
                    .setTimestamp(OffsetDateTime.now())
                    .setFooter("Requested by", event.getAuthor().getAvatarUrl())
                    .setThumbnail(jsonObject.getJSONObject("currentFavouriteCard").getJSONObject("iconUrls").getString("medium"))
                    .addField("King Level: " + cronelvl + " (" + lvlprogresspercentage + "%)", crlvlpb + (int) lvlprogress + "/" + (int) reqEXP, false)
                    .addField("STATS",
                            "**❯ Name: **" + jsonObject.getString("name") + " (" + jsonObject2.getString("tag") +") \n" +
                                    "**❯ Trophies: **" + jsonObject.getInt("trophies") + " (max. " + jsonObject.getInt("bestTrophies") + ") \n" +
                                    "**❯ Matches Won: **" + jsonObject.getInt("wins") + " (3-Crown: " + jsonObject.getInt("threeCrownWins") + ") \n" +
                                    "**❯ Matches Lost: **" + jsonObject.getInt("losses")  +" \n" +
                                    "**❯ Arena: **" + jsonObject.getJSONObject("arena").getString("name") +" \n" +
                                    "**❯ Favourite Card: **" + jsonObject.getJSONObject("currentFavouriteCard").getString("name")
                            , true)
                    .addField("MAIN DECK",
                                "**❯ 1: **" + currentDeck.getJSONObject(0).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(0).getInt("maxLevel"), currentDeck.getJSONObject(0).getInt("level")) +") \n" +
                                      "**❯ 2: **" + currentDeck.getJSONObject(1).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(1).getInt("maxLevel"), currentDeck.getJSONObject(1).getInt("level")) +") \n" +
                                      "**❯ 3: **" + currentDeck.getJSONObject(2).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(2).getInt("maxLevel"), currentDeck.getJSONObject(2).getInt("level")) +") \n" +
                                      "**❯ 4: **" + currentDeck.getJSONObject(3).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(3).getInt("maxLevel"), currentDeck.getJSONObject(3).getInt("level")) +") \n" +
                                      "**❯ 5: **" + currentDeck.getJSONObject(4).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(4).getInt("maxLevel"), currentDeck.getJSONObject(4).getInt("level")) +") \n" +
                                      "**❯ 6: **" + currentDeck.getJSONObject(5).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(5).getInt("maxLevel"), currentDeck.getJSONObject(5).getInt("level")) +") \n" +
                                      "**❯ 7: **" + currentDeck.getJSONObject(6).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(6).getInt("maxLevel"), currentDeck.getJSONObject(6).getInt("level")) +") \n" +
                                      "**❯ 8: **" + currentDeck.getJSONObject(7).getString("name") + " (" + getUntil14(currentDeck.getJSONObject(7).getInt("maxLevel"), currentDeck.getJSONObject(7).getInt("level")) +")"
                            , true)
                    .addField("CLAN",
                            "**❯ Name: **" + jsonObject.getJSONObject("clan").getString("name") + " (" + jsonObject2.getString("type") +") \n" +
                                    "**❯ Tag: **" + jsonObject.getJSONObject("clan").getString("tag")  +" \n" +
                                    "**❯ Description: **" + jsonObject2.getString("description")  +" \n" +
                                    "**❯ Role: **" + jsonObject.getString("role")  +" \n" +
                                    "**❯ Region: **" + jsonObject2.getJSONObject("location").getString("name")  +" \n" +
                                    "**❯ Members: **" + jsonObject2.getInt("members")
                            , false)
                    .build();
            event.reply(embed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUntil14(int maxLVL, int currLVL) {
        int mxLVL = switch (maxLVL) {
            case 14 -> 0;
            case 12 -> 2;
            case 9 -> 5;
            case 6 -> 8;
            default -> 0;
        };
        return currLVL + mxLVL;
    }
}
