import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksSuggester {

    private final List<Suggest> suggests = new ArrayList<>();

    public LinksSuggester(File file) throws IOException, WrongLinksFormatException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        Pattern urlPattern = Pattern.compile("(http://?|https://?|ftp://?|file://?)(www.)?[a-zA-Z][a-zA-Z0-9]+[.][a-zA-Z][a-zA-Z]+[a-zA-Z0-9/+&@#$^%=~_|]+");
        String line;
        while(true){
            line = br.readLine();
            if (line == null) break;
            List<String> words = Arrays.stream(line.split("[\s\t]+")).toList();
            System.out.println(words);

            if (words.size() < 3) {
                throw new WrongLinksFormatException("invalid string in config: " + words);
            }

            String url = words.get(words.size() - 1);
            System.out.println(url);

            Matcher urlMatcher = urlPattern.matcher(url);
            if (urlMatcher.find()) {
                System.out.println(url.substring(urlMatcher.start(), urlMatcher.end()));
            } else {
                throw new WrongLinksFormatException("invalid url in config: " + url);
            }

            StringBuilder titleSB = new StringBuilder();
            for (int i = 1; i < words.size() - 1; ++i) {
                titleSB.append(words.get(i)).append(' ');
            }
            suggests.add(new Suggest(words.get(0).toLowerCase(Locale.ROOT), titleSB.toString(), url));
        }
    }

    public List<Suggest> suggest(String text) {
        System.out.println(suggests);
        return null;
    }
}
