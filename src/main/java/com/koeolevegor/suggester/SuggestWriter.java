package com.koeolevegor.suggester;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Component;

import java.util.List;

// выделен в отдельный класс для использования Spring AOP (необходима аннотация (@Component)
@Component
public class SuggestWriter {
    public void addSuggestPage(PdfDocument doc, int pageNumber, List<Suggest> suggestList) {
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
