import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    private static String pathnameOut = "data/pdfs/";
    private static String pathnameIn = "data/converted/";
    private static String pathnameConfig = "data/config";

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            pathnameConfig = args[0];
            pathnameOut = args[1];
            pathnameIn = args[2];
        }

        LinksSuggester linksSuggester = new LinksSuggester(new File(pathnameConfig));

        File dir = new File(pathnameOut);
        // обход pdf в data/pdfs
        for (File fileIn : Objects.requireNonNull(dir.listFiles())) {
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

                    addSuggestPage(doc, i, suggestList);
                    // i увеличивается так как добавилась новая страница
                    i++;
                }
            }
        }
    }

    private static void addSuggestPage(PdfDocument doc, int pageNumber, List<Suggest> suggestList) {
        // создание новой страницы
        var newPage = doc.addNewPage(pageNumber + 2);

        // создание области редактирования
        var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
        Canvas canvas = new Canvas(newPage, rect);

        Paragraph paragraph = new Paragraph("Suggestions:\n");
        paragraph.setFontSize(25);

        // добавление ссылок
        for (Suggest suggest : suggestList) {
            PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
            PdfAction action = PdfAction.createURI(suggest.getUrl());
            annotation.setAction(action);
            Link link = new Link(suggest.getTitle(), annotation);
            paragraph.add(link.setUnderline());
            paragraph.add("\n");
        }
        canvas.add(paragraph);
        canvas.close();
    }
}
