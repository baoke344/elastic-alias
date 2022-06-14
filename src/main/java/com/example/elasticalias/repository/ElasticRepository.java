package com.example.elasticalias.repository;

import com.example.elasticalias.model.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.IOException;
import java.util.List;

public interface ElasticRepository{

    boolean isIndexExist(String indexName) throws IOException;
    void createIndex(String indexName) throws IOException;
    Post findById(String indexName, int id) throws IOException;

    void insert(String indexName, Post[] posts) throws IOException;

    void save(Post[] posts);

    Post findByIdUsingAlias(String indexName, String aliasName, int id) throws IOException;
}
