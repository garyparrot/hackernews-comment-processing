package org.example.webbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.webbackend.repositories.KeywordMatchRepository;
import org.example.webbackend.repositories.TopicRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
@Slf4j
@CrossOrigin("https://hacker-news.firebaseio.com/")
public class IndexController {

    private final KeywordMatchRepository keywordMatchRepository;
    private final TopicRepository topicRepository;


    @GetMapping("/")
    private String index(ModelMap map) {
        var topicList = topicRepository.findAll();
        var recentKeywordMatches = keywordMatchRepository.findAllByOrderByIdDesc(PageRequest.of(0, 20));

        map.addAttribute("topicList", topicList);

        return "home";
    }
}
