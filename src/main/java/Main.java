import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
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
        int standardLegendary=0, standardOther=0, wildLegendary=0, wildOther=0, arenaLegendary=0, arenaOther=0;
        String[] monthEN = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] monthRU = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        String[] wildSets = {"HOF", "NAXX", "GVG", "BRM", "TGT", "LOE", "OG", "KARA", "GANGS", "UNGORO", "ICECROWN", "LOOTAPALOOZA", "GILNEAS", "BOOMSDAY", "TROLL"};
        String[] redList = {};
        String [] yellowList = {};
        //String[] redList = {"https://playhearthstone.com/en-us/blog/22990356/", "https://playhearthstone.com/ru-ru/blog/22990356/", "This card was modified recently", "Эта карта была недавно изменена", "EVIL Miscreant", "Raiding Party", "Preparation", "Archivist Elysiana"};
        //String [] yellowList = {"https://playhearthstone.com/en-us/blog/22990355/", "https://playhearthstone.com/ru-ru/blog/22990355/", "This card will be modified soon", "Эта карта скоро будет изменена","Gloop Sprayer", "Mulchmuncher", "Necromechanic", "Flark's Boom-Zooka", "Unexpected Results", "Luna's Pocket Galaxy", "Crystology", "Glowstone Technician", "Extra Arms", "Cloning Device", "Pogo-Hopper", "Violet Haze", "The Storm Bringer", "Thunderhead", "Spirit Bomb", "Dr. Morrigan", "Security Rover", "Beryllium Nullifier"};
        outputEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-cards-rating/cards-data.php"));  //clears file every time
        outputRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone-cards-rating/cards-data.php"));
        //outputArenaEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-arena-tier-list/cards-data.php"));  //clears file every time
        HashMap<Integer, CardData> cardMap = new HashMap<>();

        String jsonString = jsonGetRequest("https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json");
        JSONArray json = new JSONArray(jsonString);

        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            CardData tempCard = new CardData();
            tempCard.setId((String) jsonObject.get("id"));
            tempCard.setDbfId((Integer) jsonObject.get("dbfId"));
            tempCard.setNameEN((String) jsonObject.get("name"));
            try {
            tempCard.setRarity((String) jsonObject.get("rarity"));
            }
            catch(Exception e) {
                tempCard.setRarity("UNCOLLECTIBLE");
            }
            tempCard.setSet((String) jsonObject.get("set"));
            tempCard.setType((String) jsonObject.get("type"));
            if (!(jsonObject.get("type").equals("HERO") && jsonObject.get("set").equals("CORE")) && !jsonObject.get("set").equals("HERO_SKINS")) {
                cardMap.put((Integer) jsonObject.get("dbfId"), tempCard);
                if (Arrays.stream(wildSets).anyMatch(tempCard.getSet()::equals))
                    if (tempCard.getRarity().equals("LEGENDARY"))
                        wildLegendary++;
                    else
                        wildOther++;
                 else
                    if (tempCard.getRarity().equals("LEGENDARY")) {
                        standardLegendary++;
                        wildLegendary++;
                    }
                    else {
                        standardOther++;
                        wildOther++;
                    }

            }
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

        jsonString = jsonGetRequest("https://hsreplay.net/api/v1/collection/?account_lo=47699632&format=json&region=2 ");
        JSONObject jsonObject_col = new JSONObject(jsonString);
        jsonObject_col = jsonObject_col.getJSONObject("collection");
        int collSum;
        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            collSum=(int) jsonObject_col.getJSONArray(Integer.toString(cardMap.get(entry.getKey()).getDbfId())).get(0) + (int) jsonObject_col.getJSONArray(Integer.toString(cardMap.get(entry.getKey()).getDbfId())).get(1);
            cardMap.get(entry.getKey()).setCollectionCopies(collSum);
        }

        JSONObject jsonObj;
        String jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_STANDARD&TimeRange=LAST_14_DAYS&RankRange=BRONZE_THROUGH_GOLD");
        String jsonStringPatch;
        String jsonStringExpansion;
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_STANDARD&TimeRange=CURRENT_PATCH&RankRange=BRONZE_THROUGH_GOLD");
        }
        catch(Exception e) {
            jsonStringPatch = jsonStringTwoWeeks;
        }
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_STANDARD&TimeRange=LAST_EXPANSION&RankRange=BRONZE_THROUGH_GOLD");
        }
        catch(Exception e) {
            jsonStringExpansion = jsonStringTwoWeeks;
        }
        JSONObject jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        int playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        JSONObject jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        int playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        JSONObject jsonObjectPatch = new JSONObject(jsonStringPatch);
        jsonObjectPatch = jsonObjectPatch.getJSONObject("series");
        jsonObjectPatch = jsonObjectPatch.getJSONObject("metadata");
        int playedPatch = (int) jsonObjectPatch.get("total_played_decks_count");
        if ((playedExpansion < playedTwoWeeks) && (playedExpansion!=0))
            if (playedExpansion< playedPatch)
                jsonObj = new JSONObject(jsonStringExpansion);
            else
                jsonObj = new JSONObject(jsonStringPatch);
        else
            if ((playedTwoWeeks<playedPatch) || (playedPatch==0))
                jsonObj = new JSONObject(jsonStringTwoWeeks);
            else
                jsonObj = new JSONObject(jsonStringPatch);
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
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_WILD&TimeRange=LAST_14_DAYS&RankRange=BRONZE_THROUGH_GOLD");
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_WILD&TimeRange=CURRENT_PATCH&RankRange=BRONZE_THROUGH_GOLD");
        }
        catch(Exception e) {
            jsonStringPatch = jsonStringTwoWeeks;
        }
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=RANKED_WILD&TimeRange=LAST_EXPANSION&RankRange=BRONZE_THROUGH_GOLD");
        }
        catch(Exception e) {
            jsonStringExpansion = jsonStringTwoWeeks;
        }
        jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        jsonObjectPatch = new JSONObject(jsonStringPatch);
        jsonObjectPatch = jsonObjectPatch.getJSONObject("series");
        jsonObjectPatch = jsonObjectPatch.getJSONObject("metadata");
        playedPatch = (int) jsonObjectPatch.get("total_played_decks_count");
        if ((playedExpansion < playedTwoWeeks) && (playedExpansion!=0))
            if (playedExpansion< playedPatch)
                jsonObj = new JSONObject(jsonStringExpansion);
            else
                jsonObj = new JSONObject(jsonStringPatch);
        else
            if ((playedTwoWeeks<playedPatch) || (playedPatch==0))
                jsonObj = new JSONObject(jsonStringTwoWeeks);
            else
                jsonObj = new JSONObject(jsonStringPatch);
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
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=ARENA&TimeRange=LAST_14_DAYS");
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=ARENA&TimeRange=CURRENT_PATCH");
        }
        catch(Exception e) {
            jsonStringPatch = jsonStringTwoWeeks;
        }
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_included_popularity_report_v2/?GameType=ARENA&TimeRange=LAST_EXPANSION");
        }
        catch(Exception e) {
            jsonStringExpansion = jsonStringTwoWeeks;
        }
        jsonObjectExpansion = new JSONObject(jsonStringExpansion);
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("series");
        jsonObjectExpansion = jsonObjectExpansion.getJSONObject("metadata");
        playedExpansion = (int) jsonObjectExpansion.get("total_played_decks_count");
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks);
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("series");
        jsonObjectTwoWeeks = jsonObjectTwoWeeks.getJSONObject("metadata");
        playedTwoWeeks = (int) jsonObjectTwoWeeks.get("total_played_decks_count");
        jsonObjectPatch = new JSONObject(jsonStringPatch);
        jsonObjectPatch = jsonObjectPatch.getJSONObject("series");
        jsonObjectPatch = jsonObjectPatch.getJSONObject("metadata");
        playedPatch = (int) jsonObjectPatch.get("total_played_decks_count");
        if ((playedExpansion < playedTwoWeeks) && (playedExpansion!=0))
            if (playedExpansion< playedPatch)
                jsonObj = new JSONObject(jsonStringExpansion);
            else
                jsonObj = new JSONObject(jsonStringPatch);
        else
            if ((playedTwoWeeks<playedPatch) || (playedPatch==0))
                jsonObj = new JSONObject(jsonStringTwoWeeks);
            else
                jsonObj = new JSONObject(jsonStringPatch);
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
                    .getWinrateStandard() * 2 * (standardLegendary+standardOther*2) / 300000);
            cardMap.get(entry.getKey()).setRatingWild(entry.getValue().getPopularityWild() * entry.getValue()
                    .getWinrateWild() * 2 * (wildLegendary+wildOther*2) / 300000);
            cardMap.get(entry.getKey()).setRatingArena(entry.getValue().getPopularityArena() * entry.getValue()
                    .getWinrateArena() * 2 * 2711 / 300000);
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingStandard() * 0.9 + entry.getValue().getRatingWild() * 0.1);
        }

        HashMap<Integer, CardData> cardMap2 = new HashMap<>();
        cardMap2 = sortByValue(cardMap);
        String colorLol;
        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            colorLol="#00000000";
//            colorLol="#64be7b";
//            double copies_calc;
//            if (entry.getValue().isWild())
//                copies_calc=entry.getValue().getCopiesWild();
//            else
//                copies_calc=entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild() * 0.1;
//            if (entry.getValue().getRarity().equals("LEGENDARY")) {
//                if (entry.getValue().getCollectionCopies() == 0)
//                    if (copies_calc == 0)
//                        colorLol = "#fdec84";
//                    else
//                        colorLol = "#f8696b";
//            } else {
//                if ((entry.getValue().getCollectionCopies() == 0 && copies_calc >= 1.5))
//                    colorLol = "#f8696b";
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc >= 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc < 1.5))
//                    colorLol = "#fba977";
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc < 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc == 0))
//                    colorLol = "#fdec84";
//                if (entry.getValue().getCollectionCopies() == 1 && copies_calc == 0)
//                    colorLol = "#b0d681";
//            }
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
                        entry.getValue().getNameEN() + (Arrays.stream(redList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+redList[0]+"\" title=\""+redList[2]+"\" style=\"color: #e80808;\">&#9888;</a>" : "")
                        + (Arrays.stream(yellowList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+yellowList[0]+"\" title=\""+yellowList[2]+"\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                        +"</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td style=\"background-color: "+colorLol+";\">" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1), entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard(), entry.getValue()
                        .getRatingWild()));
            else
                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + (Arrays.stream(redList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+redList[0]+"\" title=\""+redList[2]+"\" style=\"color: #e80808;\">&#9888;</a>" : "")
                        + (Arrays.stream(yellowList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+yellowList[0]+"\" title=\""+yellowList[2]+"\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                        + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td style=\"background-color: "+colorLol+";\">" + "%.4f" + "</td><td>" +
                        "%.6f" + "</td><td></td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue().getRatingOverall(), entry.getValue()
                        .getRatingWild()));

            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("\nEN Complete\n");
        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            colorLol="#00000000";
//            colorLol="#64be7b";
//            double copies_calc;
//            if (entry.getValue().isWild())
//                copies_calc=entry.getValue().getCopiesWild();
//            else
//                copies_calc=entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild() * 0.1;
//            if (entry.getValue().getRarity().equals("LEGENDARY")) {
//                if (entry.getValue().getCollectionCopies() == 0)
//                    if (copies_calc == 0)
//                        colorLol = "#fdec84";
//                    else
//                        colorLol = "#f8696b";
//            } else {
//                if ((entry.getValue().getCollectionCopies() == 0 && copies_calc >= 1.5))
//                    colorLol = "#f8696b";
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc >= 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc < 1.5))
//                    colorLol = "#fba977";
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc < 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc == 0))
//                    colorLol = "#fdec84";
//                if (entry.getValue().getCollectionCopies() == 1 && copies_calc == 0)
//                    colorLol = "#b0d681";
//            }
            if (!entry.getValue().isWild())
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + (Arrays.stream(redList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+redList[1]+"\" title=\""+redList[3]+"\" style=\"color: #e80808;\">&#9888;</a>" : "")
                        + (Arrays.stream(yellowList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+yellowList[1]+"\" title=\""+yellowList[3]+"\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                        + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td style=\"background-color: "+colorLol+";\">" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1), entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard(), entry.getValue()
                        .getRatingWild()));
            else
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                        + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + (Arrays.stream(redList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+redList[1]+"\" title=\""+redList[3]+"\" style=\"color: #e80808;\">&#9888;</a>" : "")
                        + (Arrays.stream(yellowList).anyMatch(entry.getValue().getNameEN()::equals) ? " <a href=\""+yellowList[1]+"\" title=\""+yellowList[3]+"\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                        + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td style=\"background-color: "+colorLol+";\">" + "%.4f" + "</td><td>" +
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

//        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
//            outputArenaEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
//                    + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                    entry.getValue().getNameEN() + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")
//            ) ? "<div class=\"legendary-star\">★</div>" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
//                    "%.6f" + "</td><td>" +
//                    "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getSet()
//                    + "</td></tr>\n", entry.getValue().getCopiesArena() * 10, entry.getValue().getRatingOverall(), entry.getValue().getRatingStandard()));
//
//        }

        dateEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone-cards-rating/current-date.php"));  //clears file every time
        dateRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone-cards-rating/current-date.php"));
        dateEN.append(monthEN[LocalDateTime.now().getMonthValue() - 1] + " " + LocalDateTime.now().getDayOfMonth() + ", " + LocalDateTime.now().getYear());
        dateRU.append(monthRU[LocalDateTime.now().getMonthValue() - 1] + " " + LocalDateTime.now().getDayOfMonth() + ", " + LocalDateTime.now().getYear());
        dateEN.close();
        dateRU.close();
        outputEN.close();
        //outputArenaEN.close();
        outputRU.close();
    }

    private static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> map) {
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
        return new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
    }

    private static String jsonGetRequest(String urlQueryString) throws IOException {
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
//            ex.printStackTrace();
            throw ex;
        }
        return json;
    }
}
