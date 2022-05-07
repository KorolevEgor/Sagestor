package com.koeolevegor.suggester;

import com.koeolevegor.suggester.exception.NotConfigureLinksSuggester;
import com.koeolevegor.suggester.exception.WrongLinksFormatException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@NoArgsConstructor
public class LinksSuggester {

    // key word to Suggest
    private final Map<String, Suggest> suggests = new HashMap<>();
    private final Pattern urlPattern =
            Pattern.compile("(http://?|https://?|ftp://?|file://?)(www.)?[a-zA-Z][a-zA-Z0-9]+[.][a-zA-Z][a-zA-Z]+[a-zA-Z0-9/+&@#$^%=~_|]+");
    private boolean isConfigure = false;

    public void configure(File file) throws IOException, WrongLinksFormatException {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String configLine;
            while (true) {
                // чтение строки из файла
                configLine = br.readLine();
                if (configLine == null) break;

                // разбиение прочитанной строки на слова по пробелу или табу
                List<String> words = Arrays.stream(configLine.split("[\s\t]+")).toList();

                // должно быть как минимум 3 слова (ключевое слово, заголовок, ссылка)
                if (words.size() < 3) {
                    throw new WrongLinksFormatException("invalid string in config: " + words);
                }

                String url = words.get(words.size() - 1);

                if (!goodUrl(url)) {
                    throw new WrongLinksFormatException("invalid url in config: " + url);
                }

                // формирование заголовка из промежуточных слов (первое слово - ключевое слово, последнее - ссылка)
                StringBuilder titleSB = new StringBuilder();
                for (int i = 1; i < words.size() - 1; ++i) {
                    titleSB.append(words.get(i)).append(' ');
                }

                String keyWord = words.get(0).toLowerCase(Locale.ROOT);
                suggests.put(
                        keyWord,
                        Suggest.builder()
                                .keyWord(keyWord)
                                .title(titleSB.toString())
                                .url(url).build());
            }
        }

        isConfigure = true;
    }

    // проверка URL на корректность
    boolean goodUrl(String url) {
        Matcher urlMatcher = urlPattern.matcher(url);
//        System.out.println(url.substring(urlMatcher.start(), urlMatcher.end()));
        return urlMatcher.find();
    }

    public List<Suggest> suggest(String text) {
        if (!isConfigure) {
            throw new NotConfigureLinksSuggester("please call the \"configure(File file)\" method");
        }
        Set<Suggest> resultSet = new HashSet<>();
        String[] words = text.toLowerCase(Locale.ROOT).split("[\s\t,.:;!?\"'()]+");
        for (String word : words) {
            if (suggests.containsKey(word)) {
                resultSet.add(suggests.get(word));
            }
        }
//        System.out.println(suggests);
        return resultSet.stream().toList();
    }
}
