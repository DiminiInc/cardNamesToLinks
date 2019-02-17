import javax.smartcardio.Card;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by kadavr95 on 23.08.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {

        HashMap<String, CardData> cardMap = new HashMap<>();
        int cardsCounter = 0;

        try (Scanner s = new Scanner(new File("src\\main\\java\\cardslist.txt"), "UTF-8")) {
            String word = new Scanner(new File("src\\main\\java\\cardslist.txt")).useDelimiter("\\Z").next();
            Pattern p = Pattern.compile("<a href=\"/cards/(\\d?\\d?\\d?\\d?\\d?\\d?)/.*?aria-label=\"(.*?)\"");
            Matcher m = p.matcher(word);
            while (m.find()) {
                if (cardMap.get(m.group(2)) == null) {
                    cardMap.put(m.group(2), new CardData(m.group(2), Integer.parseInt(m.group(1))));
                    cardsCounter++;
                }
            }
        }

        try (Scanner s = new Scanner(new File("src\\main\\java\\cardsJSON.txt"), "UTF-8")) {
            String word = new Scanner(new File("src\\main\\java\\cardsJSON.txt")).useDelimiter("\\Z").next();
            Pattern p = Pattern.compile("<Entity CardID=[\\s\\S]*?<enUS>([\\s\\S]*?)</enUS>[\\s\\S]*?<ruRU>([\\s\\S]*?)</ruRU>");
            Matcher m = p.matcher(word);
            while (m.find()) {
                if (cardMap.get(m.group(1)) != null) {
                    cardMap.get(m.group(1)).setNameRU(m.group(2));
                }
            }
        }

        try (Scanner s = new Scanner(new File("src\\main\\java\\cardsJSON"), "UTF-8")) { //enUS collectible cards for the last build
            String word = new Scanner(new File("src\\main\\java\\cardsJSON")).useDelimiter("\\Z").next();
            Pattern p = Pattern.compile("\"id\":\"([\\s\\S]*?)\",[\\s\\S]*?\"name\":\"([\\s\\S]*?)\"," +
                    "[\\s\\S]*?\"rarity\":\"([\\s\\S]*?)\",[\\s\\S]*?\"set\":\"([\\s\\S]*?)\",");
            Matcher m = p.matcher(word);
            while (m.find()) {
                if (cardMap.get(m.group(2)) != null) {
                    cardMap.get(m.group(2)).setImageID(m.group(1));
                    cardMap.get(m.group(2)).setRarity(m.group(3));
                    cardMap.get(m.group(2)).setGameSet(m.group(4));
                }
            }
        }
        String[][] rawData = new String[10][cardsCounter*2];

        try (BufferedReader br = new BufferedReader(new FileReader(new File("src\\main\\java\\cardsStandard")))) {
            String line = "";
            cardsCounter = 0;
            int counter = 0;
            boolean finNames = false;
            while (((line = br.readLine()) != null)) {
                counter++;
                if (finNames == false) {
                    cardsCounter++;
                    rawData[1][cardsCounter] = line;
                    if (line.matches("\\d?\\d?\\d?.\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?")) {
                        rawData[2][1] = line;
                        finNames = true;
                    }
                } else {
                    rawData[(counter - cardsCounter) % 6 + 2][(counter - cardsCounter) / 6 + 1] = line;
                }
            }
            for (int i = 1; i < cardsCounter; i++) {
                cardMap.get(rawData[1][i]).setWild(false);
                cardMap.get(rawData[1][i]).setPopularityStandard(Double.parseDouble(rawData[2][i]));
                cardMap.get(rawData[1][i]).setCopiesStandard(Double.parseDouble(rawData[3][i]));
                cardMap.get(rawData[1][i]).setWinrateStandard(Double.parseDouble(rawData[4][i]
                        .replace("-", "0")));
//                System.out.println(rawData[1][i] + " , " + rawData[2][i] + " , " + rawData[3][i] + " , " +
//                        rawData[4][i] + " , " + rawData[5][i] + " , " + rawData[6][i] + " , " + rawData[7][i]);
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(new File("src\\main\\java\\cardsWild")))) {
            String line = "";
            cardsCounter = 0;
            int counter = 0;
            boolean finNames = false;
            while (((line = br.readLine()) != null)) {
                counter++;
                if (finNames == false) {
                    cardsCounter++;
                    rawData[1][cardsCounter] = line;
                    if (line.matches("\\d?\\d?\\d?.\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?")) {
                        rawData[2][1] = line;
                        finNames = true;
                    }
                } else {
                    rawData[(counter - cardsCounter) % 6 + 2][(counter - cardsCounter) / 6 + 1] = line;
                }
            }
            for (int i = 1; i < cardsCounter; i++) {
                cardMap.get(rawData[1][i]).setPopularityWild(Double.parseDouble(rawData[2][i]));
                cardMap.get(rawData[1][i]).setCopiesWild(Double.parseDouble(rawData[3][i]));
                cardMap.get(rawData[1][i]).setWinrateWild(Double.parseDouble(rawData[4][i].replace
                        ("-", "0")));
//                System.out.println(rawData[1][i] + " , " + rawData[2][i] + " , " + rawData[3][i] + " , " +
//                        rawData[4][i] + " , " + rawData[5][i] + " , " + rawData[6][i] + " , " + rawData[7][i]);
            }
        }

//        try (BufferedReader br = new BufferedReader(new FileReader(new File("src\\main\\java\\cardsArena")))) {
//            String line = "";
//            cardsCounter = 0;
//            int counter = 0;
//            boolean finNames = false;
//            while (((line = br.readLine()) != null)) {
//                counter++;
//                if (finNames == false) {
//                    cardsCounter++;
//                    rawData[1][cardsCounter] = line;
//                    if (line.matches("\\d?\\d?\\d?.\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?")) {
//                        rawData[2][1] = line;
//                        finNames = true;
//                    }
//                } else {
//                    rawData[(counter - cardsCounter) % 6 + 2][(counter - cardsCounter) / 6 + 1] = line;
//                }
//            }
//            for (int i = 1; i < cardsCounter; i++) {
//                cardMap.get(rawData[1][i]).setPopularityArena(Double.parseDouble(rawData[2][i]));
//                cardMap.get(rawData[1][i]).setCopiesArena(Double.parseDouble(rawData[3][i]));
//                cardMap.get(rawData[1][i]).setWinrateArena(Double.parseDouble(rawData[4][i].replace
//                        ("-", "0")));
////                System.out.println(rawData[1][i] + " , " + rawData[2][i] + " , " + rawData[3][i] + " , " +
////                        rawData[4][i] + " , " + rawData[5][i] + " , " + rawData[6][i] + " , " + rawData[7][i]);
//            }
//        }

        for (HashMap.Entry<String, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingStandard(entry.getValue().getPopularityStandard() * entry.getValue()
                    .getWinrateStandard() * 2 * 2197 / 30);
            cardMap.get(entry.getKey()).setRatingWild(entry.getValue().getPopularityWild() * entry.getValue()
                    .getWinrateWild() * 2 * 3460 / 30);
            cardMap.get(entry.getKey()).setRatingArena(entry.getValue().getPopularityArena() * entry.getValue()
                    .getWinrateArena() * 2 * 2711 / 30);
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingStandard() * 0.9 + entry.getValue().getRatingWild() * 0.1);
        }

        HashMap<String, CardData> cardMap2 = new HashMap<>();
        cardMap2 = sortByValue(cardMap);
        for (HashMap.Entry<String, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1), entry.getValue().getRatingStandard(), entry.getValue()
                        .getRatingWild(), entry.getValue().getRatingOverall());
//                System.out.printf("<tr><td style=\"background: linear-gradient(-90deg, rgba(255,255,255,0), rgba(255,255,255,0)," +
//                        " rgba(255,255,255,0), " +
//                        "rgba(86,85,85,1), rgba(42,42,42,1), rgba(29,29,29,1)), url(https://art.hearthstonejson.com/v1/tiles/"+entry
//                        .getValue().getImageID()+".png) right -5px center; " +
//                        "\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
//                        "%.6f" + "</td><td>" + "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
//                        () * 0.1), entry.getValue().getRatingStandard(), entry.getValue()
//                        .getRatingWild(), entry.getValue().getRatingOverall());
            else
//                System.out.printf("<tr><td style=\"background: linear-gradient(-90deg, rgba(255,255,255,0), rgba(255,255,255,0),"+
//                                                " rgba(255,255,255,0), " +
//                                               "rgba(86,85,85,1), rgba(42,42,42,1), rgba(29,29,29,1)), url(https://art.hearthstonejson.com/v1/tiles/"+entry
//                        .getValue().getImageID()+".png) right -5px center; " +
//                        "\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td></td><td>" +
//                        "%.6f" + "</td><td>" + "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue()
//                        .getRatingWild(), entry.getValue().getRatingOverall());

                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td></td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue()
                        .getRatingWild(), entry.getValue().getRatingOverall());

            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("\n\n\n\n\n");
        for (HashMap.Entry<String, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
//                System.out.printf
//                        ("<tr><td style=\" background-image: url(https://art.hearthstonejson.com/v1/tiles/"+entry
//                                .getValue().getImageID()+".png); " +
//                                "\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameRU() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
//                        "%.6f" + "</td><td>" + "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
//                        () * 0.1), entry.getValue().getRatingStandard(), entry.getValue()
//                        .getRatingWild(), entry.getValue().getRatingOverall());
                System.out.printf
                        ("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                                entry.getValue().getNameRU() + "</a></td><td>" + ((entry.getValue().getRarity().equals
                                ("LEGENDARY")
                        ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                                "%.6f" + "</td><td>" + "%.6f" +
                                "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
                                + "</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                                () * 0.1), entry.getValue().getRatingStandard(), entry.getValue()
                                .getRatingWild(), entry.getValue().getRatingOverall());
            else
//                System.out.printf("<tr><td style=\" background-image: url(https://art.hearthstonejson.com/v1/tiles/"+entry
//                        .getValue().getImageID()+".png); " +
//                        "\"><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameRU() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td></td><td>" +
//                        "%.6f" + "</td><td>" + "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue()
//                        .getRatingWild(), entry.getValue().getRatingOverall());
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + "</a></td><td>" + ((entry.getValue().getRarity().equals
                        ("LEGENDARY")
                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td></td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
                        + "</td></tr>\n", entry.getValue().getCopiesWild(), entry.getValue()
                        .getRatingWild(), entry.getValue().getRatingOverall());
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("\n\n\n\n\nbeep boop beep");
        for (HashMap.Entry<String, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingArena());
        }

//        HashMap<String, CardData> cardMap3 = new HashMap<>();
//        cardMap3 = sortByValue(cardMap);
//        for (HashMap.Entry<String, CardData> entry : cardMap3.entrySet()) {
//            if (entry.getValue().getRarity().equals("LEGENDARY"))
//                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>"+ "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesArena()), entry.getValue().getRatingOverall());
//            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
//        for (HashMap.Entry<String, CardData> entry : cardMap3.entrySet()) {
//            if (entry.getValue().getRarity().equals("EPIC"))
//                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>"+ "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesArena()), entry.getValue().getRatingOverall());
//            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
//        for (HashMap.Entry<String, CardData> entry : cardMap3.entrySet()) {
//            if (entry.getValue().getRarity().equals("RARE"))
//                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>"+ "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesArena()), entry.getValue().getRatingOverall());
//            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
//        for (HashMap.Entry<String, CardData> entry : cardMap3.entrySet()) {
//            if (entry.getValue().getRarity().equals("COMMON")||entry.getValue().getRarity().equals("FREE"))
//                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
//                        entry.getValue().getNameEN() + "</a></td><td>" + ((entry.getValue().getRarity().equals
//                        ("LEGENDARY")
//                ) ? "★" : "") + "</td><td>" + "%.4f" + "</td><td>"+ "%.6f" +
//                        "</td><td>" + entry.getValue().getRarity() + "</td><td>" + entry.getValue().getGameSet()
//                        + "</td></tr>\n", (entry.getValue().getCopiesArena()), entry.getValue().getRatingOverall());
//            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//        }
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
}
