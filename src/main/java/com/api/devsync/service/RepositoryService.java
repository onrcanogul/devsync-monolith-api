package com.api.devsync.service;

import com.api.devsync.entity.Repository;

import java.util.List;

public interface RepositoryService {
    List<Repository> getByUser(String username);
}
