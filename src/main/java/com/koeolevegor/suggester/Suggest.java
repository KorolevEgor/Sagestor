package com.koeolevegor.suggester;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Suggest {
    private final String keyWord;
    private final String title;
    private final String url;
}
