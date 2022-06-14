package com.example.elasticalias.rest;

import com.example.elasticalias.model.Post;
import com.example.elasticalias.repository.ElasticRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostInterface  extends ElasticsearchRepository<Post, String >, ElasticRepository {
}
