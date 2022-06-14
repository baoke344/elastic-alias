
package com.example.elasticalias.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "id",
    "title",
    "body"
})
@Data
@Document(indexName = "baoanh", createIndex = false)
public class Post implements Serializable
{

    @JsonProperty("userId")
    @Field(type = FieldType.Integer)
    private Integer userId;
    @JsonProperty("id")
    @Id
    private Integer id;
    @JsonProperty("title")
    @Field(type = FieldType.Text)
    private String title;
    @JsonProperty("body")
    @Field(type = FieldType.Text)
    private String body;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();
    private final static long serialVersionUID = -974648545026135011L;

    @JsonAnyGetter
    private Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    private void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
