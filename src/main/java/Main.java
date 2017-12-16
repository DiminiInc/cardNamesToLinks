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

        try (Scanner s = new Scanner(new File("src\\main\\java\\cardsJSON"), "UTF-8")) {
            String word = new Scanner(new File("src\\main\\java\\cardsJSON")).useDelimiter("\\Z").next();
            Pattern p = Pattern.compile("\"name\":\"([\\s\\S]*?)\",[\\s\\S]*?\"rarity\":\"([\\s\\S]*?)\",[\\s\\S]*?\"set\":\"([\\s\\S]*?)\",");
            Matcher m = p.matcher(word);
            while (m.find()) {
                if (cardMap.get(m.group(1)) != null) {
                    cardMap.get(m.group(1)).setRarity(m.group(2));
                    cardMap.get(m.group(1)).setGameSet(m.group(3));
                }
            }
        }

        String[][] rawData = new String[10][cardsCounter + 2];

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
                cardMap.get(rawData[1][i]).setPopularityStandard(Double.parseDouble(rawData[2][i]));
                cardMap.get(rawData[1][i]).setCopiesStandard(Double.parseDouble(rawData[3][i]));
                cardMap.get(rawData[1][i]).setWinrateStandard(Double.parseDouble(rawData[4][i]
                        .replace("-","0")));
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
                cardMap.get(rawData[1][i]).setWild(true);
                cardMap.get(rawData[1][i]).setPopularityWild(Double.parseDouble(rawData[2][i]));
                cardMap.get(rawData[1][i]).setCopiesWild(Double.parseDouble(rawData[3][i]));
                cardMap.get(rawData[1][i]).setWinrateWild(Double.parseDouble(rawData[4][i].replace
                        ("-","0")));
//                System.out.println(rawData[1][i] + " , " + rawData[2][i] + " , " + rawData[3][i] + " , " +
//                        rawData[4][i] + " , " + rawData[5][i] + " , " + rawData[6][i] + " , " + rawData[7][i]);
            }
        }

        for (HashMap.Entry<String, CardData> entry : cardMap.entrySet()) {
            cardMap.get(entry.getKey()).setRatingStandard(entry.getValue().getPopularityStandard() * entry.getValue()
                    .getWinrateStandard() * 2 * 2030 / 30);
            cardMap.get(entry.getKey()).setRatingWild(entry.getValue().getPopularityWild() * entry.getValue()
                    .getWinrateWild() * 2 * 2711 / 30);
            cardMap.get(entry.getKey()).setRatingOverall(entry.getValue().getRatingStandard() * 0.9 + entry.getValue().getRatingWild() * 0.1);
        }

        HashMap<String, CardData> cardMap2 = new HashMap<>();
        cardMap2 = sortByValue(cardMap);
        for (HashMap.Entry<String, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a></td><td>"+((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "★" : "")+"</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>"+entry.getValue().getRarity()+"</td><td>"+entry.getValue().getGameSet()
                        +"</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1),entry.getValue().getRatingStandard(),entry.getValue()
                        .getRatingWild(),entry.getValue().getRatingOverall());
            else
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameEN() + "</a></td><td>"+((entry.getValue().getRarity().equals("LEGENDARY")
                ) ? "★" : "")+"</td><td>" + "%.4f" + "</td><td></td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>"+entry.getValue().getRarity()+"</td><td>"+entry.getValue().getGameSet()
                        +"</td></tr>\n", entry.getValue().getCopiesWild(),entry.getValue()
                        .getRatingWild(),entry.getValue().getRatingOverall());
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println("\n\n\n\n\n");
        for (HashMap.Entry<String, CardData> entry : cardMap2.entrySet()) {
            if (!entry.getValue().isWild())
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + "</a></td><td>"+((entry.getValue().getRarity().equals
                        ("LEGENDARY")
                ) ? "★" : "")+"</td><td>" + "%.4f" + "</td><td>" + "%.6f" + "</td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>"+entry.getValue().getRarity()+"</td><td>"+entry.getValue().getGameSet()
                        +"</td></tr>\n", (entry.getValue().getCopiesStandard() * 0.9 + entry.getValue().getCopiesWild
                        () * 0.1),entry.getValue().getRatingStandard(),entry.getValue()
                        .getRatingWild(),entry.getValue().getRatingOverall());
            else
                System.out.printf("<tr><td><a href=\"https://hsreplay.net/cards/" + entry.getValue().getId() + "\">" +
                        entry.getValue().getNameRU() + "</a></td><td>"+((entry.getValue().getRarity().equals
                        ("LEGENDARY")
                ) ? "★" : "")+"</td><td>" + "%.4f" + "</td><td></td><td>" +
                        "%.6f" + "</td><td>" + "%.6f" +
                        "</td><td>"+entry.getValue().getRarity()+"</td><td>"+entry.getValue().getGameSet()
                        +"</td></tr>\n", entry.getValue().getCopiesWild(),entry.getValue()
                        .getRatingWild(),entry.getValue().getRatingOverall());
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
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
