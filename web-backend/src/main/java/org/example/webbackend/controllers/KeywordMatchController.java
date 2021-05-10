package org.example.webbackend.controllers;

import lombok.AllArgsConstructor;
import org.example.webbackend.domain.KeywordMatch;
import org.example.webbackend.repositories.KeywordMatchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v0/keyword-matches")
public class KeywordMatchController {

    private final KeywordMatchRepository keywordMatchRepository;

    @GetMapping("/recent")
    List<KeywordMatch> recentKeywordMatches(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        return keywordMatchRepository.findAllByOrderByIdDesc(PageRequest.of(page, 20));
    }

    @GetMapping("/category/{category}")
    List<KeywordMatch> getKeywordMatchesByCategories(
            @PathVariable String category,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page) {
        return keywordMatchRepository.findKeywordMatchByCategoryOrderByIdDesc(category, PageRequest.of(page, 20));
    }

}
