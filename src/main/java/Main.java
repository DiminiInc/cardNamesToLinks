import java.io.FileReader;
import java.io.IOException;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by kadavr95 on 23.08.2017.
 */

public class Main {

    public static void main(String[] args) throws IOException {
        Writer outputRU, outputEN, outputArenaEN, dateRU, dateEN;
        String[] monthEN = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] monthRU = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        String[] wildSets = {"HOF", "NAXX", "GVG", "BRM", "TGT", "LOE", "OG", "KARA", "GANGS", "UNGORO", "ICECROWN", "LOOTAPALOOZA"};
        outputEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-cards-rating/cards-data.php"));  //clears file every time
        outputRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone-cards-rating/cards-data.php"));
        outputArenaEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-arena-tier-list/cards-data.php"));  //clears file every time
        HashMap<Integer, CardData> cardMap = new HashMap<>();

        String jsonString = jsonGetRequest("https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json");
        JSONArray json = new JSONArray(jsonString);

        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            CardData tempCard = new CardData();
            tempCard.setId((String) jsonObject.get("id"));
            tempCard.setDbfId((Integer) jsonObject.get("dbfId"));
            tempCard.setNameEN((String) jsonObject.get("name"));
            tempCard.setRarity((String) jsonObject.get("rarity"));
            tempCard.setSet((String) jsonObject.get("set"));
            tempCard.setType((String) jsonObject.get("type"));
            if (!(jsonObject.get("type").equals("HERO") && jsonObject.get("set").equals("CORE")) && !jsonObject.get("set").equals("HERO_SKINS"))
                cardMap.put((Integer) jsonObject.get("dbfId"), tempCard);
        }

        jsonString = jsonGetRequest("https://api.hearthstonejson.com/v1/latest/ruRU/cards.collectible.json");
        json = new JSONArray(jsonString);
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (!(jsonObject.get("type").equals("HERO") && jsonObject.get("set").equals("CORE")) && !jsonObject.get("set").equals("HERO_SKINS")) {
                cardMap.get((Integer) jsonObject.get("dbfId")).setNameRU((String) jsonObject.get("name"));
                if (Arrays.stream(wildSets).anyMatch(cardMap.get((Integer) jsonObject.get("dbfId")).getSet()::equals))
                    cardMap.get((Integer) jsonObject.get("dbfId")).setWild(true);
                else
                    cardMap.get((Integer) jsonObject.get("dbfId")).setWild(false);
            }
        }

        JSONObject jsonObj;
        String jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=RANKED_STANDARD&TimeRange=CURRENT_EXPANSION&RankRange=ALL");
        String jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=RANKED_STANDARD&TimeRange=LAST_14_DAYS&RankRange=ALL");
        JSONObject jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        int playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        JSONObject jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        int playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        if (playedExpansion < playedTwoWeeks)
            jsonObj = new JSONObject(jsonStringExpansion);
        else
            jsonObj = new JSONObject(jsonStringTwoWeeks);
        jsonObj = jsonObj.getJSONObject("series");
        jsonObj = jsonObj.getJSONObject("data");
        json = jsonObj.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityStandard((double) jsonObject.get("popularity"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateStandard((double) jsonObject.get("winrate"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesStandard((double) jsonObject.get("count"));
            }
        }
        jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=RANKED_WILD&TimeRange=CURRENT_EXPANSION&RankRange=ALL");
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=RANKED_WILD&TimeRange=LAST_14_DAYS&RankRange=ALL");
        jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        if (playedExpansion < playedTwoWeeks)
            jsonObj = new JSONObject(jsonStringExpansion);
        else
            jsonObj = new JSONObject(jsonStringTwoWeeks);
        jsonObj = jsonObj.getJSONObject("series");
        jsonObj = jsonObj.getJSONObject("data");
        json = jsonObj.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityWild((double) jsonObject.get("popularity"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateWild((double) jsonObject.get("winrate"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesWild((double) jsonObject.get("count"));
            }
        }
        jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=ARENA&TimeRange=CURRENT_EXPANSION&RankRange=ALL");
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report/?GameType=ARENA&TimeRange=LAST_14_DAYS&RankRange=ALL");
        jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        if (playedExpansion < playedTwoWeeks)
            jsonObj = new JSONObject(jsonStringExpansion);
        else
            jsonObj = new JSONObject(jsonStringTwoWeeks);
        jsonObj = jsonObj.getJSONObject("series");
        jsonObj = jsonObj.getJSONObject("data");
        json = jsonObj.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityArena((double) jsonObject.get("popularity"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateArena((double) jsonObject.get("winrate"));
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesArena((double) jsonObject.get("count"));
            }
        }


        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingStandard(entry.getValue().getPopularityStandard() * entry.getValue()
                    .getWinrateStandard() * 2 * 1703 / 300000);
            cardMap.get(entry.getKey()).setRatingWild(entry.getValue().getPopularityWild() * entry.getValue()
                    .getWinrateWild() * 2 * 3707 / 300000);
            cardMap.get(entry.getKey()).setRatingArena(entry.getValue().getPopularityArena() * entry.getValue()
                    .getWinrateArena() * 2 * 2711 / 300000);
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingStandard() * 0.9 + entry.getValue().getRatingWild() * 0.1);
        }

        HashMap<Integer, CardData> cardMap2 = new HashMap<>();
        cardMap2 = sortByValue(cardMap);

        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
//                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
//                        + entry.getValue().getImageID() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + " " + ((entry.getValue().getGameSet().equals("UNGORO") ||
//                        entry.getValue().getGameSet().equals("ICECROWN") || entry.getValue().getGameSet().equals("LOOTAPALOOZA")
//                ) ? "<a href=\"https://playhearthstone.com/en-us/blog/22912682\" " +
//                        "title=\"This card will be rotated out of Standard soon\" style=\"color: #ffd633;\">&#9888;</a>"
//                        : "") + " " + ((entry.getValue().getNameEN().equals("Baku the Mooneater") ||
//                        entry.getValue().getNameEN().equals("Genn Greymane") || entry.getValue().getNameEN().equals("Gloom Stag") ||
//                        entry.getValue().getNameEN().equals("Glitter Moth") || entry.getValue().getNameEN().equals("Murkspark Eel") ||
//                        entry.getValue().getNameEN().equals("Black Cat") || entry.getValue().getNameEN().equals("Naturalize") ||
//                        entry.getValue().getNameEN().equals("Doomguard") || entry.getValue().getNameEN().equals("Divine Favor")
//                ) ? "<a href=\"https://playhearthstone.com/en-us/blog/22912682\" " +
//                        "title=\"This card will be moved to Hall of Fame soon\" style=\"color: #e80808;\">&#9888;</a>"
//                        : "") + "</a>"+((entry.getValue().getRarity().equals("LEGENDARY")
//                ) ? "<div class=\"legendary-star\">★</div>" : "")+"</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
//                        "%.6f" + "</td><td>" + "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
//                        () * 0.1), entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard(), entry.getValue()
//                        .getRatingWild()));
                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1), entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard(), entry.getValue()
                        .getRatingWild()));
            else
                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" +
                        "%.6f" + "</td><td></td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue().getRatingOverall(), entry.getValue()
                        .getRatingWild()));

            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("\nEN Complete\n");
        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1), entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard(), entry.getValue()
                        .getRatingWild()));
            else
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" +
                        "%.6f" + "</td><td></td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue().getRatingOverall(), entry.getValue()
                        .getRatingWild()));
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }


        System.out.println("\nRU Complete!\n");

        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingArena());
        }

        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            outputArenaEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                    + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                    entry.getValue().getNameEN() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
            ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                    "%.6f" + "</td><td>" +
                    "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                    + "</td></tr>\n", entry.getValue().getCopiesArena() * 10, entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard()));

        }

        dateEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-cards-rating/current-date.php"));  //clears file every time
        dateRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone-cards-rating/current-date.php"));
        dateEN.append(monthEN[LocalDateTime.now().getMonthValue() - 1] + " " + LocalDateTime.now().getDayOfMonth() + ", " + LocalDateTime.now().getYear());
        dateRU.append(monthRU[LocalDateTime.now().getMonthValue() - 1] + " " + LocalDateTime.now().getDayOfMonth() + ", " + LocalDateTime.now().getYear());
        dateEN.close();
        dateRU.close();
        outputEN.close();
        outputArenaEN.close();
        outputRU.close();
    }

    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    public static String jsonGetRequest(String urlQueryString) {
        String json = null;
        System.setProperty("http.agent", "Chrome");
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
