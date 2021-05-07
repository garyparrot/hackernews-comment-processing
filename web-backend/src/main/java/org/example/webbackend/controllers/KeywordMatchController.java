package org.example.webbackend.controllers;

import lombok.AllArgsConstructor;
import org.example.webbackend.domain.KeywordMatch;
import org.example.webbackend.repositories.KeywordMatchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v0/keyword-matches")
public class KeywordMatchController {

    private final KeywordMatchRepository keywordMatchRepository;

    @GetMapping("/recent")
    List<KeywordMatch> recentKeywordMatches(@RequestParam int page) {
        return keywordMatchRepository.findAllByOrderByIdDesc(PageRequest.of(page, 20));
    }

}
