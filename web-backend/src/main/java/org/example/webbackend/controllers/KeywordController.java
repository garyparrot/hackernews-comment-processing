package org.example.webbackend.controllers;

import lombok.AllArgsConstructor;
import org.example.webbackend.domain.Keyword;
import org.example.webbackend.repositories.KeywordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.client.ResponseProcessingException;

@Controller
@AllArgsConstructor
@RequestMapping("/keywords")
public class KeywordController {

    private final KeywordRepository keywordRepository;

    @GetMapping
    public String showKeywords(@RequestParam(value = "page", required = false, defaultValue = "0") int page, ModelMap model) {
        if(page < 0) {
            page = 0;
        }

        model.put("keywordList", keywordRepository.findAll());

        return "keyword-table";
    }

    @GetMapping("/add")
    public String addKeywords() {
        return "new-item";
    }

    @PostMapping("/add")
    public String save(@ModelAttribute("keyword") String keyword, @ModelAttribute("categories") String categories) {
        if(keyword == null || keyword.equals("") || categories == null || categories.equals(""))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Keyword k = new Keyword();
        k.setKeyword(keyword);
        k.setCategory(categories.split(" "));

        keywordRepository.save(k);
        return "redirect:/keywords";
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.POST, RequestMethod.DELETE})
    public String delete(@PathVariable int id) {

        keywordRepository.deleteById(id);

        return "redirect:/keywords";
    }

}
