import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzes a txt file and prints the distribution of the user's OS, Country, etc.
 */
public class Analyzer {
    private final List<Parser> parsers;
    private final String fileName;

    public Analyzer(String fileName, List<Parser> parsers) {
        this.fileName = fileName;
        this.parsers = parsers;
    }

    public void analyze() {
        HashMap<String,HashMap<String,Integer>> res = getParserMaps();

        print(res);
    }

    private void print(HashMap<String, HashMap<String, Integer>> res) {
        for (Map.Entry<String, HashMap<String, Integer>> category : res.entrySet()) {
            System.out.println("---" + category.getKey() + "---");

            int count = getCount(category);
            for (Map.Entry<String, Integer> field: category.getValue().entrySet()) {
                double percentage = (double)field.getValue() / count * 100;

                if (percentage != 0)
                    System.out.println(field.getKey() + ": " + String.format("%.2f", percentage) + "%");
            }
        }
    }

    private int getCount(Map.Entry<String, HashMap<String, Integer>> category) {
        int count = 0;
        for (Map.Entry<String, Integer> type: category.getValue().entrySet()) {
            count += type.getValue();
        }

        return count;
    }

    private HashMap<String,HashMap<String,Integer>> getParserMaps() {
        HashMap<String,HashMap<String,Integer>> res = new HashMap<>();

        for (Parser parser : parsers) {
            String parserName = parser.getName();

            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                HashMap<String, Integer> map = new HashMap<>();

                while ((line = br.readLine()) != null) {
                    String type = parser.parse(line);
                    int count = map.containsKey(type) ? map.get(type) : 0;
                    map.put(type, count + 1);
                }

                res.put(parserName, sort(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    private HashMap<String, Integer> sort(HashMap<String, Integer> res) {
        return res.entrySet()
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

