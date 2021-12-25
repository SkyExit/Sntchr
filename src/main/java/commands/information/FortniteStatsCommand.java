package commands.information;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FortniteStatsCommand extends Command {
    public FortniteStatsCommand() {
        this.name = "fnstats";
        this.help = "Shows your Fortnite stats!";
        this.category = new Category("Information");
    }
    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            event.reply("Please use s&fnstats <name> <epic/ps4/xbox>!");
        } else {
            String[] args = event.getArgs().split(" ");
            String accountName = args[0];
            String accountPlatform = switch (args[1]) {
                case "epic" -> "epic";
                case "ps4" -> "psn";
                case "xbox" -> "xbl";
                default -> "epic";
            };


            //URL BUILDER
            URL url = null;
            int responsecode = 0;

            try {
                URL baseURL = new URL("https://fortnite-api.com/v2/stats/br/v2?image=all&");
                //Append Name
                if(args[0] != null) {
                    String buildBaseURL = baseURL.toString() + "name=" + args[0].toString() + "&";
                            buildBaseURL = buildBaseURL + "accountType=" + accountPlatform;

                    url = new URL(buildBaseURL);
                    System.out.println(buildBaseURL);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }


            //API REQUESTER

            //80c834244d1130d8cc61bdd6f5d600c8b6fb2483
            try {
                assert url != null;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer 006fcf81-1dfa-4466-8b52-658ec362b570");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestMethod("GET");
                conn.connect();

                responsecode = conn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //DATA MINER
            try {
                if (responsecode != 200) {
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                } else {

                    String inline = "";
                    Scanner scanner = new Scanner(url.openStream());

                    //Write all the JSON data into a string using a scanner
                    while (scanner.hasNext()) {
                        inline += scanner.nextLine();
                    }

                    //Close the scanner
                    scanner.close();

                    //Using the JSON simple library parse the string into a json object
                    JSONObject data_obj = new JSONObject(inline);

                    //Get the required object from the above created object
                    JSONObject obj = (JSONObject) data_obj.get("Global");

                    //Get the required data using its key
                    //System.out.println(obj.get("TotalRecovered"));
                    System.out.println(data_obj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            /*

            try {

                URL url = new URL("https://fortnite-api.com/v2/stats/br/v2?name=N3M0Y&accountType=epic&image=all");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("Authorization", "006fcf81-1dfa-4466-8b52-658ec362b570");

                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                out.flush();
                out.close();

                con.setRequestProperty("Content-Type", "application/json");
                String contentType = con.getHeaderField("Content-Type");

                con.setConnectTimeout(5000);
                con.setReadTimeout(5000);

                int status = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();

                System.out.println(content);

            } catch (ProtocolException ignored) {

            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }

             */
        }
    }
}
