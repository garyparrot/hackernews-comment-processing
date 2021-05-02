package org.example.webbackend.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.webbackend.domain.Topic;
import org.example.webbackend.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {

    private final TopicRepository topicRepository;

    @GetMapping("/")
    private String index(ModelMap map) {
        var topicList = topicRepository.findAll();

        map.addAttribute("topicList", topicList);

        return "home";
    }
}
