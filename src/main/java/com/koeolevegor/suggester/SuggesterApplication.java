package com.koeolevegor.suggester;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class SuggesterApplication {
    private static String pathnameOut = "data/pdfs/";
    private static String pathnameIn = "data/converted/";
    private static String pathnameConfig = "data/config";

    private static ApplicationContext context;

    @Autowired
    public void setContext(ApplicationContext context) {
        SuggesterApplication.context = context;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            pathnameConfig = args[0];
            pathnameOut = args[1];
            pathnameIn = args[2];
        }
        SpringApplication.run(SuggesterApplication.class);
        runSuggester(pathnameConfig);
    }

    private static void runSuggester(String pathnameConfig) throws IOException {
        LinksSuggester linksSuggester = context.getBean(LinksSuggester.class);
        linksSuggester.configure(new File(pathnameConfig));
        SuggestWriter suggestWriter = context.getBean(SuggestWriter.class);
//        LinksSuggester linksSuggester = new LinksSuggester(new File(pathnameConfig));

        File dir = new File(pathnameOut);
        // обход pdf в data/pdfs
        for (File fileIn : Objects.requireNonNull(dir.listFiles())) {
            Set<Suggest> globalSuggestList = new HashSet<>();
            // открытие (или создание) файла на запись
            File fileOut = new File(pathnameIn + fileIn.getName());

            try (var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(fileOut))) {
                // обход всех страниц документа
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    // получение списка Suggest по тексту страницы
                    List<Suggest> suggestList = linksSuggester.suggest(
                            PdfTextExtractor.getTextFromPage(doc.getPage(i + 1)));
                    // если на текущей странице не встретилось ни одно ключевое слово
                    if (suggestList.isEmpty())
                        continue;

                    List<Suggest> finalSuggestList = new ArrayList<>();
                    for (Suggest suggest : suggestList) {
                        if (!globalSuggestList.contains(suggest)) {
                            finalSuggestList.add(suggest);
                            globalSuggestList.add(suggest);
                        }
                    }
                    globalSuggestList.addAll(finalSuggestList);

                    if (finalSuggestList.isEmpty())
                        continue;

                    suggestWriter.addSuggestPage(doc, i, finalSuggestList);
                    // i увеличивается так как добавилась новая страница
                    i++;
                }
            }
        }
    }


}
