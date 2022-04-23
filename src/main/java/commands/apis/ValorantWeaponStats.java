package commands.apis;

import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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

    private enum WeaponType {
        HEAVY,
        RIFLE,
        SHOTGUN,
        SIDEARM,
        SNIPER,
        SMG,
        MELEE
    }

    private static WeaponType categories;

    private void setCategory(int weapon) {
        categories = switch (weapon) {
            case 0, 1 -> WeaponType.HEAVY;
            case 2, 3, 4 -> WeaponType.RIFLE;
            case 5, 6 -> WeaponType.SHOTGUN;
            case 7, 8, 9, 10, 11 -> WeaponType.SIDEARM;
            case 12, 13, 14 -> WeaponType.SNIPER;
            case 15, 16 -> WeaponType.SMG;
            case 17 -> WeaponType.MELEE;
            default -> throw new IllegalStateException("Unexpected value: " + weapon);
        };
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply(true).queue();

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
        assert jsonObject != null;
        JSONObject weaponData = jsonObject.getJSONArray("data").getJSONObject(Integer.parseInt(event.getOption("weapon").getAsString()));
        //event.getHook().sendMessage(weaponData.getString("displayName")).queue();
        event.getHook().sendMessageEmbeds(embedBuilder(categories, weaponData)).setEphemeral(true).queue();
    }

    private MessageEmbed embedBuilder(WeaponType categories, JSONObject weaponData) {

        JSONObject weaponStats = null;
        JSONObject shopData;
        JSONArray skins;
        JSONArray damageRanges;

        EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Weapon Data: " + weaponData.getString("displayName"));
            embed.setThumbnail(weaponData.getString("displayIcon"));

        try {
            weaponStats = weaponData.getJSONObject("weaponStats");
            shopData = weaponData.getJSONObject("shopData");
            skins = weaponData.getJSONArray("skins");
            damageRanges = weaponStats.getJSONArray("damageRanges");
        } catch (JSONException e) {
            return embed.build();
        }

            if(categories != WeaponType.MELEE) {
                String[] wallPenetration = weaponStats.getString("wallPenetration").split("::");
                embed.addField("weaponStats",
                        "fire rate: " + weaponStats.getInt("fireRate") + "\n" +
                                "magazine size: " + weaponStats.getInt("magazineSize") + "\n" +
                                "run speed multiplier: " + weaponStats.getDouble("runSpeedMultiplier") + "\n" +
                                "reload speed: " + weaponStats.getDouble("reloadTimeSeconds") + "s \n" +
                                "wall penetration: " + wallPenetration[1] + "\n"
                        , true);
                switch (categories) { case HEAVY, RIFLE, SMG -> embed.addField(createADS(weaponStats.getJSONObject("adsStats"))); }
                embed.addBlankField(false);
                createDamageRanges(embed, damageRanges.length(), damageRanges);
            }
            return embed.build();
    }

    private MessageEmbed.Field createADS(JSONObject adsStats) {
        return new MessageEmbed.Field("ADS Stats",
                "zoom multiplier: " + adsStats.getDouble("zoomMultiplier") + "\n" +
                        "fire rate: " + adsStats.getDouble("fireRate") + "\n" +
                        "run speed multiplier: " + adsStats.getDouble("runSpeedMultiplier") + "\n"
                , true);
    }

    private void createDamageRanges(EmbedBuilder embed, int range, JSONArray damageRanges) {
        for (int i = 0; i < range; i++) {
            JSONObject dmgRange = damageRanges.getJSONObject(i);
            embed.addField("Range " + i,
                    "range start: " + dmgRange.getInt("rangeStartMeters") + "m \n" +
                            "range end: " + dmgRange.getInt("rangeEndMeters") + "m \n" +
                            "head damage: " + dmgRange.getDouble("headDamage") + " \n" +
                            "body damage: " + dmgRange.getDouble("bodyDamage") + " \n" +
                            "leg damage: " + dmgRange.getDouble("legDamage")
                    , true);
        }
    }
}
