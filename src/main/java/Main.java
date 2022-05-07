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
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // создаём конфиг
        LinksSuggester linksSuggester = new LinksSuggester(new File("data/config"));
//        System.out.println(linksSuggester.suggest("Тестовый текст. Класс, джава; объект. Spring"));

        var dir = new File("data/pdfs");
        // обход pdf в data/pdfs
        for (var fileIn : dir.listFiles()) {
            File fileOut = new File("data/converted/" + fileIn.getName());
            var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(fileOut));
            // обход всех страниц документа
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                List<Suggest> suggestList = linksSuggester.suggest(PdfTextExtractor.getTextFromPage(doc.getPage(i + 1)));
                // если на текущей странице не встретилось ни одно ключевое слово
                if (suggestList.isEmpty())
                    continue;

                // создание новой страницы
                var newPage = doc.addNewPage(i + 2);
                i++;

                var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
                Canvas canvas = new Canvas(newPage, rect);
                Paragraph paragraph = new Paragraph("Suggestions:\n");
                paragraph.setFontSize(25);

                for (int j = 0; j < suggestList.size(); ++j) {
                    PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                    PdfAction action = PdfAction.createURI(suggestList.get(j).getUrl());
                    annotation.setAction(action);
                    Link link = new Link(suggestList.get(j).getTitle(), annotation);
                    paragraph.add(link.setUnderline());
                    paragraph.add("\n");
                }
                canvas.add(paragraph);
                canvas.close();
            }
            doc.close();
        }
    }
}
