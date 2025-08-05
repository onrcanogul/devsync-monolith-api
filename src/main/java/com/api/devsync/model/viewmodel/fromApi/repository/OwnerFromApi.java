package com.api.devsync.model.viewmodel.fromApi.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OwnerFromApi {
    private String login;
    private long id;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("html_url")
    private String htmlUrl;
}
