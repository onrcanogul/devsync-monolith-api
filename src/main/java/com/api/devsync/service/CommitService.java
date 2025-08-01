package com.api.devsync.service;

import com.api.devsync.entity.Commit;

import java.util.List;

public interface CommitService {
    List<Commit> get();
    Commit getByHash(String hash);
}
