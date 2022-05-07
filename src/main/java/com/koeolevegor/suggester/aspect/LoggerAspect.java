package com.koeolevegor.suggester.aspect;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.koeolevegor.suggester.Suggest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Aspect
public class LoggerAspect {
    @After("execution(public void addSuggestPage(" +
            "com.itextpdf.kernel.pdf.PdfDocument," +
            "int," +
            "java.util.List<com.koeolevegor.suggester.Suggest>))")
    public void addSuggestPageBeforeAdvice(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        PdfDocument doc = (PdfDocument) args[0];
        int pageNumber = (Integer) args[1];

        StringBuilder log = new StringBuilder();
        log
                .append("Filename: ").append(doc.getDocumentInfo().getTitle()).append('\n')
                .append("Page number: ").append(pageNumber/2 + 1).append('\n')
                .append("*************************************************************************************");
        System.out.println(log);
    }

    @Around("execution(public java.util.List<com.koeolevegor.suggester.Suggest> suggest(String))")
    public List<Suggest> suggestAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        List<Suggest> suggestList = (List<Suggest>) proceedingJoinPoint.proceed();

        StringBuilder log = new StringBuilder();
        log
                .append('[').append(new Date()).append(']').append('\n')
                .append("Suggests: ")
                .append(suggestList.toString());
        System.out.println(log);

        return suggestList;
    }
}
