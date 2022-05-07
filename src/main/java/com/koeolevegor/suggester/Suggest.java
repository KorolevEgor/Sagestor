package com.koeolevegor.suggester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class Suggest {
    private final String keyWord;
    private final String title;
    private final String url;
}
