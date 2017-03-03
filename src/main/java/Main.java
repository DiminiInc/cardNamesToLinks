import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
//https://github.com/osobolev/hashtest/blob/master/src/main/java/ru/mirea/hash/BarSample.java
/**
 * Created by admin on 10.02.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

        try (Scanner s = new Scanner(new File("C:\\Users\\kadav\\IdeaProjects\\wordcounter\\src\\main\\java\\gorod.txt"), "UTF-8")) {
            while (s.hasNext()) {
                String word = s.next().toLowerCase();
                if (wordMap.get(word) != null) {
                    wordMap.put(word, wordMap.get(word) + 1);
                } else {
                    wordMap.put(word, 1);
                }

            }
        }

        for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
            System.out.println("Word =  " + entry.getKey() + " Quantity = " + entry.getValue());
        }

        ArrayList<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(wordMap.entrySet());
        list.sort((e1,e2)->-e1.getValue().compareTo(e2.getValue()));

        int sum=0;

        for (Map.Entry<String, Integer> entry : list) {
            System.out.println("Word =  " + entry.getKey() + " Quantity = " + entry.getValue());
            sum+=entry.getValue();
        }

        System.out.println("Total quantity: "+sum);

        DefaultCategoryDataset data = new DefaultCategoryDataset();
        String category = "Words frequency";
        for (Map.Entry<String, Integer> entry : list) {
            if (entry.getValue()>=sum/100)
                data.addValue(entry.getValue(),category,entry.getKey());
            else
                break;
        }

        JFreeChart chart = ChartFactory.createBarChart("Words frequency", "Word", "Quantity", data);
        BufferedImage image = chart.createBufferedImage(600, 400);
        File file = new File("chart.png");
        ImageIO.write(image, "png", file);
        Desktop.getDesktop().open(file);
    }
}
