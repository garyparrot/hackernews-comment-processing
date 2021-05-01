package org.example.keywords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordAndCategories {

    private String keyword;
    private String[] categories;

    public Iterable<KeywordAndCategory> asPairs() {
        return Arrays.stream(categories)
                .map((category) -> new KeywordAndCategory(keyword, category))
                .collect(Collectors.toList());
    }

}
