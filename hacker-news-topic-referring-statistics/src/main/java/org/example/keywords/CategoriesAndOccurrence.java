package org.example.keywords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesAndOccurrence {

    private long occurrenceId;
    private String[] categories;

    public Iterable<CategoryAndOccurrence> asPairs() {
        return Arrays.stream(categories)
                .map((category) -> new CategoryAndOccurrence(category, occurrenceId))
                .collect(Collectors.toList());
    }
}
