package com.example.elasticalias.controller;

import com.example.elasticalias.model.Post;
import com.example.elasticalias.services.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public void savePost() throws IOException {
        postService.savePost("baoanh_1");
    }

    @GetMapping("/id")
    @ResponseBody
    public Post getPost(@RequestParam String id) throws IOException {
        return postService.getPostFromES("baoanh", id);
    }

    @GetMapping("/alias")
    @ResponseBody
    public Post getPostByAlias(@RequestParam String id) throws IOException {
        return postService.saveWithAlias("baoanh", "baoanh-alias", id);
    }
}
