import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by kadavr95 on 23.08.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

        try (Scanner s = new Scanner(new File("src\\main\\java\\cardslist.txt"), "UTF-8")) {
            String word="";
            while (s.hasNext()) {
                word = word+s.next()+" ";
            }
            Pattern p = Pattern.compile("<a href=\"/cards/(\\d?\\d?\\d?\\d?\\d?\\d?)/.*?aria-label=\"(.*?)\"");
            Matcher m = p.matcher(word);
            while (m.find()) {
                if (wordMap.get(m.group(2)) == null) {
                    wordMap.put(m.group(2), Integer.parseInt(m.group(1)));
                }
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(new File("src\\main\\java\\cardscolumn.txt")))) {
            String line="";
            while (((line = br.readLine()) != null)) {
                if (wordMap.get(line) != null) {
                    line=line.replace(line,"<a href=\"https://hsreplay.net/cards/"+wordMap.get(line)+"\">"+line+"</a>");
                    System.out.println(line);
                }
            }

        }
    }
}
