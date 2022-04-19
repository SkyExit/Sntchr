package commands.apis;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Scanner;

public class ValorantWeaponStats extends SlashCommand {
    public ValorantWeaponStats() {
        this.name = "valo";
        this.help = "Returns the mentioned users Avatar";
        this.category = new Category("Information");

        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "weapon", "The member you want the avatar from")
                .addChoice("Odin", "0")
                .addChoice("Ares", "1")
                .addChoice("Vandal", "2")
                .addChoice("Bulldog", "3")
                .addChoice("Phantom", "4")
                .addChoice("Judge", "5")
                .addChoice("Bucky", "6")
                .addChoice("Frenzy", "7")
                .addChoice("Classic", "8")
                .addChoice("Ghost", "9")
                .addChoice("Sheriff", "10")
                .addChoice("Shorty", "11")
                .addChoice("Operator", "12")
                .addChoice("Guardian", "13")
                .addChoice("Marshal", "14")
                .addChoice("Spectre", "15")
                .addChoice("Stinger", "16")
                .addChoice("Melee", "17")
                .setRequired(true)
        );
    }

    private enum Categories {
        HEAVY,
        RIFLE,
        SHOTGUN,
        SIDEARM,
        SNIPER,
        SMG,
        MELEE
    }

    private static Categories categories;

    private void setCategory(int weapon) {
        categories = switch (weapon) {
            case 0, 1 -> Categories.HEAVY;
            case 2, 3, 4 -> Categories.RIFLE;
            case 5, 6 -> Categories.SHOTGUN;
            case 7, 8, 9, 10, 11 -> Categories.SIDEARM;
            case 12, 13, 14 -> Categories.SNIPER;
            case 15, 16 -> Categories.SMG;
            case 17 -> Categories.MELEE;
            default -> throw new IllegalStateException("Unexpected value: " + weapon);
        };
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        //API REQUESTER
        Response response = null;
        int responsecode = 0;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            URL url = new URL("https://valorant-api.com/v1/weapons");

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ")
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
        setCategory(Integer.parseInt(event.getOption("weapon").getAsString()));
        JSONObject weaponData = jsonObject.getJSONArray("data").getJSONObject(Integer.parseInt(event.getOption("weapon").getAsString()));
        event.getHook().sendMessage(weaponData.getString("displayName")).queue();
    }

    private MessageEmbed embedBuilder(Categories categories, JSONObject weaponData) {

        JSONObject weaponStats = weaponData.getJSONObject("weaponStats");
        JSONObject shopData = weaponData.getJSONObject("shopData");
        JSONObject skins = weaponData.getJSONObject("skins");

        EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Weapon Data: " + weaponData.getString("displayName"));
            embed.setThumbnail(weaponData.getString("displayIcon"));

            switch (categories) {
                case HEAVY: {
                    embed.addField("weaponStats",
                            "fire rate: " + weaponStats.getInt("fireRate") + "\n" +
                                    "magazine size: " + weaponStats.getInt("fireRate") + "\n" +
                                    "run speed multiplier: " + weaponStats.getInt("fireRate") + "\n" +
                                    "reload speed: " + weaponStats.getInt("fireRate") + "\n" +
                                    "wall penetration: " + weaponStats.getInt("fireRate") + "\n"
                            , true);
                }
            }

            return embed.build();
    }
}
