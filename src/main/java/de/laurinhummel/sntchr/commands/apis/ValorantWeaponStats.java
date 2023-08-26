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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class ValorantWeaponStats extends SlashCommand {
    public ValorantWeaponStats() {
        this.name = "valo";
        this.help = "Gives useful information about VALORANT";
        this.category = new Category("Information");

        this.children = new SlashCommand[]{new Weapon(), new Skins()};
    }

    private static JSONObject getJSONObject() {
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
        return jsonObject;
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

    private static void setCategory(int weapon) {
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

    }

    private static class Weapon extends SlashCommand {
        public Weapon() {
            this.name = "weapon";
            this.help = "weapon information";

            this.options = Collections.singletonList(new OptionData(OptionType.STRING, "weapon", "The weapon's name")
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
                    .addChoice("Knife", "17")
                    .setRequired(true)
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();

            setCategory(Integer.parseInt(event.getOption("weapon").getAsString()));
            assert getJSONObject() != null;
            JSONObject weaponData = getJSONObject().getJSONArray("data").getJSONObject(Integer.parseInt(event.getOption("weapon").getAsString()));

            JSONObject weaponStats = null;
            JSONObject shopData;
            JSONArray skins;
            JSONArray damageRanges;

            EmbedBuilder embed = new EmbedBuilder();

            embed.setThumbnail(weaponData.getString("displayIcon"));

            if(categories != WeaponType.MELEE) {
                weaponStats = weaponData.getJSONObject("weaponStats");
                shopData = weaponData.getJSONObject("shopData");
                skins = weaponData.getJSONArray("skins");
                damageRanges = weaponStats.getJSONArray("damageRanges");

                embed.setTitle("Weapon Data: " + weaponData.getString("displayName") + " (" + shopData.getInt("cost") + ")");

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
            } else {
                embed.setTitle("Weapon Data: " + weaponData.getString("displayName"));
            }

            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }

    private static class Skins extends SlashCommand {
        public Skins() {
            this.name = "skins";
            this.help = "weapon skins";

            this.options = Collections.singletonList(new OptionData(OptionType.STRING, "weapon", "The weapon's name")
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
                    .addChoice("Knife", "17")
                    .setRequired(true)
            );
        }

        @Override
        protected void execute(SlashCommandEvent event) {
            event.deferReply(true).queue();

            JSONObject weaponData = getJSONObject().getJSONArray("data").getJSONObject(Integer.parseInt(event.getOption("weapon").getAsString()));
            JSONArray skins = weaponData.getJSONArray("skins");

            EmbedBuilder embed = new EmbedBuilder();
                embed.setThumbnail(weaponData.getString("displayIcon"));

            StringBuilder stringBuilder = new StringBuilder();
            String[] skinString;
            for (int i = 0; i < skins.length(); i++) {
                JSONObject skinObject = skins.getJSONObject(i);
                if(Objects.equals(skinObject.get("displayIcon").toString(), "null")) {
                    stringBuilder.append(skinObject.getString("displayName")).append("\n");
                } else {
                    stringBuilder.append("[").append(skinObject.getString("displayName")).append("](").append(skinObject.get("displayIcon")).append(") \n");
                }
            }

            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
        }
    }

    private static void createSkins(EmbedBuilder embed, JSONArray skins) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        for (int i = 0; i < skins.length(); i++) {
            JSONObject skinObject = skins.getJSONObject(i);
            if(stringBuilder.length() < 400) {
                stringBuilder.append(skinObject.getString("displayName")).append("\n");
            } else {
                stringBuilder2.append(skinObject.getString("displayName")).append("\n");
            }
        }

        embed.addField("Skins", stringBuilder.toString(), true);
        if(!stringBuilder2.isEmpty()) {
            embed.addField("Skins", stringBuilder2.toString(), true);
        }
    }

    private static MessageEmbed.Field createADS(JSONObject adsStats) {
        return new MessageEmbed.Field("ADS Stats",
                "zoom multiplier: " + adsStats.getDouble("zoomMultiplier") + "\n" +
                        "fire rate: " + adsStats.getDouble("fireRate") + "\n" +
                        "run speed multiplier: " + adsStats.getDouble("runSpeedMultiplier") + "\n"
                , true);
    }

    private static void createDamageRanges(EmbedBuilder embed, int range, JSONArray damageRanges) {
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
