import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by kadavr95 on 23.08.2017.
 */

public class Main {

    public static void main(String[] args) throws IOException {
        Writer outputRU, outputEN, outputArenaEN, outputArenaRU, dateRU, dateEN;
        int standardLegendary = 0, standardOther = 0, wildLegendary = 0, wildOther = 0,
                arenaLegendary = 0, arenaOther = 0;
        String[] monthEN = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] monthRU = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        String[] wildSets = {"EXPERT1", "LEGACY", "NAXX", "GVG",
                "BRM", "TGT", "LOE",
                "OG", "KARA", "GANGS",
                "UNGORO", "ICECROWN", "LOOTAPALOOZA",
                "GILNEAS", "BOOMSDAY", "TROLL",
                "DALARAN", "ULDUM", "DRAGONS", "YEAR_OF_THE_DRAGON",
                "DEMON_HUNTER_INITIATE", "BLACK_TEMPLE", "SCHOLOMANCE", "DARKMOON_FAIRE", "DARKMOON_FAIRE_MINI_SET"
        };
//        String[] arenaSets = {"CORE", "EXPERT1", "HOF", "NAXX", "GVG",
//                "BRM", "TGT", "LOE",
//                "OG", "KARA", "GANGS",
//                "UNGORO", "ICECROWN", "LOOTAPALOOZA",
//                "GILNEAS", "BOOMSDAY", "TROLL",
//                "DALARAN", "ULDUM", "DRAGONS", "YEAR_OF_THE_DRAGON",
//                "DEMON_HUNTER_INITIATE", "BLACK_TEMPLE", "SCHOLOMANCE", "DARKMOON_FAIRE", "DARKMOON_FAIRE_MINI_SET",
//                "THE_BARRENS", "THE_BARRENS_MINI_SET", "STORMWIND", "STORMWIND_MINI_SET", "ALTERAC_VALLEY", "ALTERAC_VALLEY_MINI_SET",
//                "THE_SUNKEN_CITY", "THE_SUNKEN_CITY_MINI_SET", "REVENDRETH"
//        };
        String[] recentChangesList = {};
        String[] incomingChangesList = {};
        String[] hallOfFameList = {};
        String[] setRotationList = {};
//        String[] recentChangesList = {"https://playhearthstone.com/news/23426180", "https://playhearthstone.com/news/23426180",
//                "This card was modified recently", "Эта карта была недавно изменена",
//                "Aldor Attendant", "Torrent", "Shattered Rumbler", "The Lurker Below", "Priestess of Fury", "Crimson Sigil Runner", "Scavenger's Ingenuity", "Shadowjeweler Hanar", "Blackjack Stunner", "Imprisoned Scrap Imp", "Bloodboil Brute", "Bloodsworn Mercenary"};
//        String[] incomingChangesList = {"https://us.forums.blizzard.com/en/hearthstone/t/176-balance-updates/36815", "https://us.forums.blizzard.com/en/hearthstone/t/176-balance-updates/36815",
//                "This card will be modified soon", "Эта карта скоро будет изменена",
//                "Dragonqueen Alexstrasza", "Corsair Cache", "Metamorphosis", "Kayn Sunfury", "Warglaives of Azzinoth", "Dragoncaster", "Fungal Fortunes", "Galakrond, the Nightmare"};
//        String[] hallOfFameList = {"https://playhearthstone.com/news/23426180", "https://playhearthstone.com/news/23426180",
//                "This card will be moved to Hall of Fame soon", "Эта карта скоро будет перенесена в Зал Славы",
//                "Boulderfist Ogre"};
//        String[] setRotationList = {"https://playhearthstone.com/news/23426180", "https://playhearthstone.com/news/23426180",
//                "This card will be rotated out of Standard soon", "Эта карта скоро будет доступна только в Вольном режиме",
//                "GILNEAS", "BOOMSDAY", "TROLL"};
        outputEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone/crafting-guide/cards-data.php"));
        outputRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone/crafting-guide/cards-data.php"));
        outputArenaEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone/arena-tier-list/cards-data.php"));
        outputArenaRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone/arena-tier-list/cards-data.php"));
        HashMap<Integer, CardData> cardMap = new HashMap<>();

        /* Get cards data */
        String jsonString = jsonGetRequest("https://api.hearthstonejson.com/v1/latest/enUS/cards.collectible.json");
        JSONArray json = new JSONArray(jsonString);
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            CardData tempCard = new CardData();
            tempCard.setId((String) jsonObject.get("id"));
            tempCard.setDbfId((Integer) jsonObject.get("dbfId"));
            tempCard.setNameEN((String) jsonObject.get("name"));
            if (jsonObject.has("classes")) {
                tempCard.setClasses(jsonObject.getJSONArray("classes"));
            }
            try {
                tempCard.setRarity((String) jsonObject.get("rarity"));
            } catch (Exception e) {
                tempCard.setRarity("UNCOLLECTIBLE");
            }
            if (jsonObject.has("set")) {
                tempCard.setSet(jsonObject.get("set").toString());
            }
            tempCard.setType((String) jsonObject.get("type"));
            tempCard.setCardClass((String) jsonObject.get("cardClass"));
            if (jsonObject.has("set") &&
                    !(jsonObject.get("type").equals("HERO") && jsonObject.get("set").equals("CORE")) &&
                    !jsonObject.get("set").equals("HERO_SKINS")) {
                cardMap.put((Integer) jsonObject.get("dbfId"), tempCard);
                if (Arrays.asList(wildSets).contains(tempCard.getSet()))
                    if (tempCard.getRarity().equals("LEGENDARY"))
                        wildLegendary++;
                    else
                        wildOther++;
                else if (tempCard.getRarity().equals("LEGENDARY")) {
                    standardLegendary++;
                    wildLegendary++;
                } else {
                    standardOther++;
                    wildOther++;
                }
//                if (Arrays.asList(arenaSets).contains(tempCard.getSet()))
                if (tempCard.getRarity().equals("LEGENDARY"))
                    arenaLegendary++;
                else
                    arenaOther++;
            }
        }

        /* Add Russian localization data */
        jsonString = jsonGetRequest("https://api.hearthstonejson.com/v1/latest/ruRU/cards.collectible.json");
        json = new JSONArray(jsonString);
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (jsonObject.has("set") &&
                    !(jsonObject.get("type").equals("HERO") && jsonObject.get("set").equals("CORE")) &&
                    !jsonObject.get("set").equals("HERO_SKINS")) {
                cardMap.get((Integer) jsonObject.get("dbfId")).setNameRU((String) jsonObject.get("name"));
                cardMap.get((Integer) jsonObject.get("dbfId")).setWild(Arrays.asList(wildSets).contains(cardMap.get((Integer) jsonObject.get("dbfId")).getSet()));
            }
        }

        /* Get user collection */
        jsonString = jsonGetRequest("https://hsreplay.net/api/v1/collection/?account_lo=47699632&format=json&region=2 ");
        JSONObject jsonObject_col = new JSONObject(jsonString);
        jsonObject_col = jsonObject_col.getJSONObject("collection");
        int collSum;
        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            if (jsonObject_col.has(Integer.toString(cardMap.get(entry.getKey()).getDbfId()))) {
                collSum = (int) jsonObject_col.getJSONArray(Integer.toString(cardMap.get(entry.getKey()).getDbfId())).get(0) +
                        (int) jsonObject_col.getJSONArray(Integer.toString(cardMap.get(entry.getKey()).getDbfId())).get(1) +
                        (int) jsonObject_col.getJSONArray(Integer.toString(cardMap.get(entry.getKey()).getDbfId())).get(2);
                cardMap.get(entry.getKey()).setCollectionCopies(collSum);
            }
        }

        JSONObject jsonObj, jsonObjectTwoWeeks, jsonObjectPatch, jsonObjectExpansion;
        String jsonStringTwoWeeks, jsonStringPatch, jsonStringExpansion;
        int playedTwoWeeks, playedPatch, playedExpansion;

        /* Get Standard stats */
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_STANDARD&TimeRange=LAST_14_DAYS&RankRange=BRONZE_THROUGH_GOLD");
        playedTwoWeeks = 0;
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
        json = jsonObjectTwoWeeks.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            playedTwoWeeks += (int) jsonObject.get("times_played");
        }

        jsonStringPatch = "{}";
        playedPatch = 0;
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_STANDARD&TimeRange=CURRENT_PATCH&RankRange=BRONZE_THROUGH_GOLD");
            jsonObjectPatch = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            json = jsonObjectPatch.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedPatch += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        jsonStringExpansion = "{}";
        playedExpansion = 0;
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_STANDARD&TimeRange=CURRENT_EXPANSION&RankRange=BRONZE_THROUGH_GOLD");
            jsonObjectExpansion = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
            json = jsonObjectExpansion.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedExpansion += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        int playedStandard;
        if ((playedExpansion < playedTwoWeeks) && (playedExpansion != 0))
            if ((playedPatch < playedExpansion) && (playedPatch != 0)) {
                jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
                playedStandard = playedPatch;
            } else {
                jsonObj = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
                playedStandard = playedExpansion;
            }
        else if ((playedPatch < playedTwoWeeks) && (playedPatch != 0)) {
            jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            playedStandard = playedPatch;
        } else {
            jsonObj = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
            playedStandard = playedTwoWeeks;
        }
        json = jsonObj.getJSONArray("ALL");

        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityStandard(((BigDecimal) jsonObject.get("included_popularity")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateStandard(((BigDecimal) jsonObject.get("included_winrate")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesStandard(((BigDecimal) jsonObject.get("included_count")).doubleValue());
            }
        }

        /* Get Wild stats */
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_WILD&TimeRange=LAST_14_DAYS&RankRange=BRONZE_THROUGH_GOLD");
        playedTwoWeeks = 0;
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
        json = jsonObjectTwoWeeks.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            playedTwoWeeks += (int) jsonObject.get("times_played");
        }

        jsonStringPatch = "{}";
        playedPatch = 0;
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_WILD&TimeRange=CURRENT_PATCH&RankRange=BRONZE_THROUGH_GOLD");
            jsonObjectPatch = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            json = jsonObjectPatch.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedPatch += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        jsonStringExpansion = "{}";
        playedExpansion = 0;
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=RANKED_WILD&TimeRange=CURRENT_EXPANSION&RankRange=BRONZE_THROUGH_GOLD");
            jsonObjectExpansion = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
            json = jsonObjectExpansion.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedExpansion += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        int playedWild;
        if ((playedExpansion < playedTwoWeeks) && (playedExpansion != 0))
            if ((playedPatch < playedExpansion) && (playedPatch != 0)) {
                jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
                playedWild = playedPatch;
            } else {
                jsonObj = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
                playedWild = playedExpansion;
            }
        else if ((playedPatch < playedTwoWeeks) && (playedPatch != 0)) {
            jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            playedWild = playedPatch;
        } else {
            jsonObj = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
            playedWild = playedTwoWeeks;
        }
        json = jsonObj.getJSONArray("ALL");

        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityWild(((BigDecimal) jsonObject.get("included_popularity")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateWild(((BigDecimal) jsonObject.get("included_winrate")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesWild(((BigDecimal) jsonObject.get("included_count")).doubleValue());
            }
        }

        /* Get Arena stats */
        jsonStringTwoWeeks = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=ARENA&TimeRange=LAST_14_DAYS");
        playedTwoWeeks = 0;
        jsonObjectTwoWeeks = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
        json = jsonObjectTwoWeeks.getJSONArray("ALL");
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            playedTwoWeeks += (int) jsonObject.get("times_played");
        }

        jsonStringPatch = "{}";
        playedPatch = 0;
        try {
            jsonStringPatch = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=ARENA&TimeRange=CURRENT_PATCH");
            jsonObjectPatch = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            json = jsonObjectPatch.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedPatch += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        jsonStringExpansion = "{}";
        playedExpansion = 0;
        try {
            jsonStringExpansion = jsonGetRequest("https://hsreplay.net/analytics/query/card_list_free/?GameType=ARENA&TimeRange=CURRENT_EXPANSION");
            jsonObjectExpansion = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
            json = jsonObjectExpansion.getJSONArray("ALL");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                playedExpansion += (int) jsonObject.get("times_played");
            }
        } catch (Exception ignored) {
        }

        if ((playedExpansion < playedTwoWeeks) && (playedExpansion != 0))
            if ((playedPatch < playedExpansion) && (playedPatch != 0))
                jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
            else
                jsonObj = new JSONObject(jsonStringExpansion).getJSONObject("series").getJSONObject("data");
        else if ((playedPatch < playedTwoWeeks) && (playedPatch != 0))
            jsonObj = new JSONObject(jsonStringPatch).getJSONObject("series").getJSONObject("data");
        else
            jsonObj = new JSONObject(jsonStringTwoWeeks).getJSONObject("series").getJSONObject("data");
        json = jsonObj.getJSONArray("ALL");

        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if (cardMap.containsKey((Integer) jsonObject.get("dbf_id"))) {
                cardMap.get((Integer) jsonObject.get("dbf_id")).setPopularityArena(((BigDecimal) jsonObject.get("included_popularity")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setWinrateArena(((BigDecimal) jsonObject.get("included_winrate")).doubleValue());
                cardMap.get((Integer) jsonObject.get("dbf_id")).setCopiesArena(((BigDecimal) jsonObject.get("included_count")).doubleValue());
            }
        }

        /* Calculate rating */
        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingStandard(entry.getValue().getPopularityStandard() * entry.getValue()
                    .getWinrateStandard() * 2 * (standardLegendary + standardOther * 2) / 300000);
            cardMap.get(entry.getKey()).setRatingWild(entry.getValue().getPopularityWild() * entry.getValue()
                    .getWinrateWild() * 2 * (wildLegendary + wildOther * 2) / 300000);
            cardMap.get(entry.getKey()).setRatingArena(entry.getValue().getPopularityArena() * entry.getValue()
                    .getWinrateArena() * 2 * (arenaLegendary + arenaOther * 2) / 300000);
            cardMap.get(entry.getKey()).setRatingOverall(
                    entry.getValue().getRatingStandard() * playedStandard / (playedStandard + playedWild)
                            + entry.getValue().getRatingWild() * playedWild / (playedStandard + playedWild));
            cardMap.get(entry.getKey()).setCopiesOverall(
                    entry.getValue().getCopiesStandard() * playedStandard / (playedStandard + playedWild)
                            + entry.getValue().getCopiesWild() * playedWild / (playedStandard + playedWild));
        }

        /* Generate HTML for the crafting guide */
        HashMap<Integer, CardData> cardMap2;
        cardMap2 = sortByValue(cardMap);
        String cardCopiesColorState;
        String cardCopiesState;
        StringBuilder cardClassesText;
        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            cardCopiesColorState = "#00000000";
            cardCopiesState = "OK";
            /* Excluded code for the displaying of user collection */
//            cardCopiesColorState = "#64be7b";
//            double copies_calc;
//            if (entry.getValue().isWild())
//                copies_calc = entry.getValue().getCopiesWild();
//            else
//                copies_calc = entry.getValue().getCopiesOverall();
//            if (entry.getValue().getRarity().equals("LEGENDARY")) {
//                if (entry.getValue().getCollectionCopies() == 0)
//                    if (copies_calc == 0) {
//                        cardCopiesColorState = "#fdec84";
//                        cardCopiesState = "MISSING_MODERATE";
//                    } else {
//                        cardCopiesColorState = "#f8696b";
//                        cardCopiesState = "MISSING_SEVERE";
//                    }
//                if (entry.getValue().getCollectionCopies() > 1 && !entry.getValue().getSet().equals("CORE")) {
//                    cardCopiesColorState = "#2e69fd";
//                    cardCopiesState = "EXTRA";
//                }
//            } else {
//                if ((entry.getValue().getCollectionCopies() == 0 && copies_calc >= 1.5)) {
//                    cardCopiesColorState = "#f8696b";
//                    cardCopiesState = "MISSING_SEVERE";
//                }
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc >= 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc < 1.5)) {
//                    cardCopiesColorState = "#fba977";
//                    cardCopiesState = "MISSING_HIGH";
//                }
//                if ((entry.getValue().getCollectionCopies() == 1 && copies_calc < 1.5) || (entry.getValue().getCollectionCopies() == 0 && copies_calc == 0)) {
//                    cardCopiesColorState = "#fdec84";
//                    cardCopiesState = "MISSING_MODERATE";
//                }
//                if (entry.getValue().getCollectionCopies() == 1 && copies_calc == 0) {
//                    cardCopiesColorState = "#b0d681";
//                    cardCopiesState = "MISSING_LOW";
//                }
//                if (entry.getValue().getCollectionCopies() > 2 && !entry.getValue().getSet().equals("CORE")) {
//                    cardCopiesColorState = "#2e69fd";
//                    cardCopiesState = "EXTRA";
//                }
//            }
            cardClassesText = new StringBuilder();
            if (entry.getValue().getClasses() != null) {
                for (int i = 0; i < entry.getValue().getClasses().length(); i++) {
                    cardClassesText.append("class:").append(entry.getValue().getClasses().optString(i)).append(", ");
                }
                cardClassesText.setLength(cardClassesText.length() - 2);
            } else {
                cardClassesText.append("class:").append(entry.getValue().getCardClass());
            }
            
            if (!entry.getValue().isWild())
                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                                + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                                + entry.getValue().getId() + "\">" + entry.getValue().getNameEN()
                                + (Arrays.asList(recentChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + recentChangesList[0] + "\" title=\"" + recentChangesList[2] + "\" style=\"color: #e80808;\">&#9888;</a>" : "")
                                + (Arrays.asList(incomingChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + incomingChangesList[0] + "\" title=\"" + incomingChangesList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(hallOfFameList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + hallOfFameList[0] + "\" title=\"" + hallOfFameList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(setRotationList).contains(entry.getValue().getSet()) ? " <a href=\"" + setRotationList[0] + "\" title=\"" + setRotationList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                                + "</td><td style=\"background-color: " + cardCopiesColorState + ";\">" + "%.4f"
                                + "</td><td>" + "%.6f" + "</td><td>" + "%.6f" + "</td><td>" + "%.6f" +
                                "</td><td>rarity:" + entry.getValue().getRarity()
                                + "</td><td>set:" + entry.getValue().getSet()
                                + "</td><td>" + cardClassesText
                                + "</td><td>collection:" + cardCopiesState + "</td></tr>\n",
                        entry.getValue().getCopiesOverall(), entry.getValue().getRatingOverall(),
                        entry.getValue().getRatingStandard(), entry.getValue().getRatingWild()));
            else
                outputEN.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                                + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                                + entry.getValue().getId() + "\">" + entry.getValue().getNameEN()
                                + (Arrays.asList(recentChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + recentChangesList[0] + "\" title=\"" + recentChangesList[2] + "\" style=\"color: #e80808;\">&#9888;</a>" : "")
                                + (Arrays.asList(incomingChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + incomingChangesList[0] + "\" title=\"" + incomingChangesList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(hallOfFameList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + hallOfFameList[0] + "\" title=\"" + hallOfFameList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(setRotationList).contains(entry.getValue().getSet()) ? " <a href=\"" + setRotationList[0] + "\" title=\"" + setRotationList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                                + "</td><td style=\"background-color: " + cardCopiesColorState + ";\">" + "%.4f" + "</td><td>"
                                + "%.6f" + "</td><td></td><td>" + "%.6f" +
                                "</td><td>rarity:" + entry.getValue().getRarity()
                                + "</td><td>set:" + entry.getValue().getSet()
                                + "</td><td>" + cardClassesText
                                + "</td><td>collection:" + cardCopiesState + "</td></tr>\n",
                        entry.getValue().getCopiesWild(), entry.getValue().getRatingOverall(),
                        entry.getValue().getRatingWild()));

            if (!entry.getValue().isWild())
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                                + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                                + entry.getValue().getId() + "\">" + entry.getValue().getNameRU()
                                + (Arrays.asList(recentChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + recentChangesList[1] + "\" title=\"" + recentChangesList[3] + "\" style=\"color: #e80808;\">&#9888;</a>" : "")
                                + (Arrays.asList(incomingChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + incomingChangesList[1] + "\" title=\"" + incomingChangesList[3] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(hallOfFameList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + hallOfFameList[0] + "\" title=\"" + hallOfFameList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(setRotationList).contains(entry.getValue().getSet()) ? " <a href=\"" + setRotationList[0] + "\" title=\"" + setRotationList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                                + "</td><td style=\"background-color: " + cardCopiesColorState + ";\">" + "%.4f" + "</td><td>"
                                + "%.6f" + "</td><td>" + "%.6f" + "</td><td>" + "%.6f" +
                                "</td><td>rarity:" + entry.getValue().getRarity()
                                + "</td><td>set:" + entry.getValue().getSet()
                                + "</td><td>" + cardClassesText
                                + "</td><td>collection:" + cardCopiesState + "</td></tr>\n",
                        entry.getValue().getCopiesOverall(), entry.getValue().getRatingOverall(),
                        entry.getValue().getRatingStandard(), entry.getValue().getRatingWild()));
            else
                outputRU.append(String.format("<tr><td class=\"lazyload\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                                + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                                + entry.getValue().getId() + "\">" + entry.getValue().getNameRU()
                                + (Arrays.asList(recentChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + recentChangesList[1] + "\" title=\"" + recentChangesList[3] + "\" style=\"color: #e80808;\">&#9888;</a>" : "")
                                + (Arrays.asList(incomingChangesList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + incomingChangesList[1] + "\" title=\"" + incomingChangesList[3] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(hallOfFameList).contains(entry.getValue().getNameEN()) ? " <a href=\"" + hallOfFameList[0] + "\" title=\"" + hallOfFameList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + (Arrays.asList(setRotationList).contains(entry.getValue().getSet()) ? " <a href=\"" + setRotationList[0] + "\" title=\"" + setRotationList[2] + "\" style=\"color: #ffd633;\">&#9888;</a>" : "")
                                + "</a>" + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                                + "</td><td style=\"background-color: " + cardCopiesColorState + ";\">" + "%.4f" + "</td><td>" +
                                "%.6f" + "</td><td></td><td>" + "%.6f" +
                                "</td><td>rarity:" + entry.getValue().getRarity()
                                + "</td><td>set:" + entry.getValue().getSet()
                                + "</td><td>" + cardClassesText
                                + "</td><td>collection:" + cardCopiesState + "</td></tr>\n",
                        entry.getValue().getCopiesWild(), entry.getValue().getRatingOverall(),
                        entry.getValue().getRatingWild()));
        }

        System.out.println("\nComplete crafting guide!\n");

        /* Generate HTML for the arena tierlist */
        for (HashMap.Entry<Integer, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingArena());
        }
        cardMap2 = sortByValue(cardMap);

        for (HashMap.Entry<Integer, CardData> entry : cardMap2.entrySet()) {
            cardClassesText = new StringBuilder();
            if (entry.getValue().getClasses() != null) {
                for (int i = 0; i < entry.getValue().getClasses().length(); i++) {
                    cardClassesText.append("class:" + entry.getValue().getClasses().optString(i) + ", ");
                }
                cardClassesText.setLength(cardClassesText.length() - 2);
            } else {
                cardClassesText.append("class:").append(entry.getValue().getCardClass());
            }

//            if (Arrays.asList(arenaSets).contains(entry.getValue().getSet()))
            outputArenaEN.append(String.format("<tr><td class=\"lazyload extended-gradient\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                            + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                            + entry.getValue().getId() + "\">" + entry.getValue().getNameEN() + "</a>"
                            + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                            + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" +
                            "</td><td>rarity:" + entry.getValue().getRarity()
                            + "</td><td>set:" + entry.getValue().getSet()
                            + "</td><td>" + cardClassesText
                            + "</td></tr>\n",
                    entry.getValue().getCopiesArena(), entry.getValue().getRatingOverall()));
//            if (Arrays.asList(arenaSets).contains(entry.getValue().getSet()))
            outputArenaRU.append(String.format("<tr><td class=\"lazyload extended-gradient\" data-bg=\"https://art.hearthstonejson.com/v1/tiles/"
                            + entry.getValue().getId() + ".png\"><a href=\"https://hsreplay.net/cards/"
                            + entry.getValue().getId() + "\">" + entry.getValue().getNameRU() + "</a>"
                            + ((entry.getValue().getRarity().equals("LEGENDARY")) ? "<div class=\"legendary-star\">★</div>" : "")
                            + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" +
                            "</td><td>rarity:" + entry.getValue().getRarity()
                            + "</td><td>set:" + entry.getValue().getSet()
                            + "</td><td>" + cardClassesText
                            + "</td></tr>\n",
                    entry.getValue().getCopiesArena(), entry.getValue().getRatingOverall()));
        }

        System.out.println("\nComplete drafting guide!\n");

        /* Files generation */
        dateEN = new BufferedWriter(new FileWriter("../Site Dev/en/statistics/games/hearthstone/crafting-guide/current-date.php"));
        dateRU = new BufferedWriter(new FileWriter("../Site Dev/ru/statistics/games/hearthstone/crafting-guide/current-date.php"));
        dateEN.append(monthEN[LocalDateTime.now().getMonthValue() - 1]).append(" ").append(String.valueOf(LocalDateTime.now().getDayOfMonth())).append(", ").append(String.valueOf(LocalDateTime.now().getYear()));
        dateRU.append(monthRU[LocalDateTime.now().getMonthValue() - 1]).append(" ").append(String.valueOf(LocalDateTime.now().getDayOfMonth())).append(", ").append(String.valueOf(LocalDateTime.now().getYear()));
        dateEN.close();
        dateRU.close();

        Files.copy(Paths.get("../Site Dev/en/statistics/games/hearthstone/crafting-guide/current-date.php"), Paths.get("../Site Dev/en/statistics/games/hearthstone/arena-tier-list/current-date.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/ru/statistics/games/hearthstone/crafting-guide/current-date.php"), Paths.get("../Site Dev/ru/statistics/games/hearthstone/arena-tier-list/current-date.php"), REPLACE_EXISTING);

        outputEN.close();
        outputArenaEN.close();
        outputRU.close();
        outputArenaRU.close();

        Files.copy(Paths.get("../Site Dev/en/statistics/games/hearthstone/crafting-guide/current-date.php"), Paths.get("../Site Release/en/statistics/games/hearthstone/crafting-guide/current-date.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/ru/statistics/games/hearthstone/crafting-guide/current-date.php"), Paths.get("../Site Release/ru/statistics/games/hearthstone/crafting-guide/current-date.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/en/statistics/games/hearthstone/arena-tier-list/current-date.php"), Paths.get("../Site Release/en/statistics/games/hearthstone/arena-tier-list/current-date.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/ru/statistics/games/hearthstone/arena-tier-list/current-date.php"), Paths.get("../Site Release/ru/statistics/games/hearthstone/arena-tier-list/current-date.php"), REPLACE_EXISTING);

        Files.copy(Paths.get("../Site Dev/en/statistics/games/hearthstone/crafting-guide/cards-data.php"), Paths.get("../Site Release/en/statistics/games/hearthstone/crafting-guide/cards-data.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/ru/statistics/games/hearthstone/crafting-guide/cards-data.php"), Paths.get("../Site Release/ru/statistics/games/hearthstone/crafting-guide/cards-data.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/en/statistics/games/hearthstone/arena-tier-list/cards-data.php"), Paths.get("../Site Release/en/statistics/games/hearthstone/arena-tier-list/cards-data.php"), REPLACE_EXISTING);
        Files.copy(Paths.get("../Site Dev/ru/statistics/games/hearthstone/arena-tier-list/cards-data.php"), Paths.get("../Site Release/ru/statistics/games/hearthstone/arena-tier-list/cards-data.php"), REPLACE_EXISTING);

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
        String json;
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
            json = streamToString(inStream);
        } catch (IOException ex) {
            throw ex;
        }
        return json;
    }
}
