package com.example.elasticalias.services;

import com.example.elasticalias.model.Post;
import com.example.elasticalias.rest.PostInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
@AllArgsConstructor
public class PostService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();
    private final PostInterface elasticRepository;

    public Post[] getPostsFromSource() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("https://jsonplaceholder.typicode.com/posts", String.class);
        String data = response.getBody();

        return mapper.readValue(data, Post[].class);
    }

    public void savePost(String indexName) throws IOException {
        Post[] posts = getPostsFromSource();
        elasticRepository.insert(indexName, posts);
    }

    public Post getPostFromES(String indexName, String id) throws IOException {
        return elasticRepository.findById(indexName, Integer.parseInt(id));
    }

//    public Post getPostByAlias(String indexName, String id) {
//        return
//    }

    public Post saveWithAlias(String indexName, String aliasName, String id) throws IOException {
        return elasticRepository.findByIdUsingAlias(indexName, aliasName, Integer.parseInt(id));
    }
}
