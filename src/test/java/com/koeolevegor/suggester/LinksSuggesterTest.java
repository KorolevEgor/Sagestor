package com.koeolevegor.suggester;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class LinksSuggesterTest {

    LinksSuggester linksSuggester;

    LinksSuggesterTest() throws IOException {
        linksSuggester = new LinksSuggester();
        linksSuggester.configure(new File("data/config"));
    }

    @Test
    public void suggestTestEmpty() {
        Assertions.assertEquals(linksSuggester.suggest(""), new ArrayList<>());
    }

    @Test
    public void suggestTest1() {
        Assertions.assertEquals(
                linksSuggester.suggest("java"),
                List.of(Suggest.builder()
                        .keyWord("java")
                        .title("The Best Java course")
                        .url("http://example.org/java")
                        .build()));
    }

    @Test
    public void suggestTestTrash() {
        Assertions.assertEquals(
                linksSuggester.suggest("  . java,,,,,,//"),
                List.of(Suggest.builder()
                        .keyWord("java")
                        .title("The Best Java course")
                        .url("http://example.org/java")
                        .build()));
    }

    @Test
    public void suggestTest2Obj() {
        Assertions.assertEquals(
                linksSuggester.suggest("  . java,,,,сканер,,//"),
                List.of(
                    Suggest.builder()
                        .keyWord("сканер")
                        .title("How to use java.util.Scanner?")
                        .url("http://example.org/scanner")
                        .build(),
                    Suggest.builder()
                            .keyWord("java")
                            .title("The Best Java course")
                            .url("http://example.org/java")
                            .build()
                        ));
    }

}