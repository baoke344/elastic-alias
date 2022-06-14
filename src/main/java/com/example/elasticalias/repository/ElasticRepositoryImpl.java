package com.example.elasticalias.repository;

import com.example.elasticalias.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor
@Service
public class ElasticRepositoryImpl implements ElasticRepository{

    private final RestHighLevelClient client;
    private static final ObjectMapper mapper = new ObjectMapper();

    private final ElasticsearchOperations operations;

    public boolean isIndexExist(String indexName) throws IOException {
        if (StringUtils.isEmpty(indexName)) {
            return false;
        }
        var request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);

    }

    public void createIndex(String indexName) throws IOException {
        if (StringUtils.isEmpty(indexName)) {
            return;
        }
        if (isIndexExist(indexName)) {
            return;
        }
        var request = new CreateIndexRequest(indexName);
        // Setting
        Map<String, Object> settings = new HashMap<>();
        settings.put("number_of_shards", 1);
        settings.put("number_of_replicas", 1);
        request.settings(settings);

        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        IndicesAliasesRequest aliasRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                        .index(indexName)
                        .alias("baoanh-alias");
        aliasRequest.addAliasAction(action);
        AcknowledgedResponse aliasResponse = client.indices().updateAliases(aliasRequest, RequestOptions.DEFAULT);
    }

    @Override
    public Post findById(String indexName, int id) throws IOException {
        if(!isIndexExist(indexName)) {
            createIndex(indexName);
        }

        List<Post> result = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("id",id));


        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        try {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if (searchHits.length > 0) {
                Arrays.stream(searchHits)
                        .forEach(hit -> result.add(mapper.convertValue(hit.getSourceAsMap(), Post.class)));
            }
        } catch (Exception ex) {
            System.out.println("Error when converting search result "+ ex.getMessage());
        }
        return result.get(0);
    }

    public void insert(String indexName, Post[] posts) throws IOException {
        if (StringUtils.isEmpty(indexName) || posts == null) {
            return;
        }

        if(!isIndexExist(indexName)) {
            createIndex(indexName);
        }

        var request = new BulkRequest();

        for( Post post : posts) {
            request.add(
                    new IndexRequest(indexName)
                            .id(String.valueOf(post.getId()))
                            .source(mapper.convertValue(post, Map.class)));
        }
        request.timeout(TimeValue.timeValueMinutes(2));
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        var bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        List<String> messages = new ArrayList<>();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    var failure = bulkItemResponse.getFailure();
                    messages.add(failure.getMessage());
                }
            }
            System.out.println("Insert product fail: "+ messages);
        }
    }

    public Post findByIdUsingAlias(String indexName, String aliasName, int id) throws IOException {
        if(!isIndexExist(indexName)) {
            createIndex(indexName);
        }

        List<Post> result = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(aliasName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("id",id));
//        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
//        boolQuery.must(boolQueryBuilder);

//        NativeQueryBuilder builder = new NativeQueryBuilder().withQuery(boolQueryBuilder).
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        try {
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if (searchHits.length > 0) {
                Arrays.stream(searchHits)
                        .forEach(hit -> result.add(mapper.convertValue(hit.getSourceAsMap(), Post.class)));
            }
        } catch (Exception ex) {
            System.out.println("Error when converting search result "+ ex.getMessage());
        }
        return result.get(0);
    }

    public void save(Post[] posts) {
        operations.save(posts);
    }
}
