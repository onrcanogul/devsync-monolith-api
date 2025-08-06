package com.api.devsync.service.impl;

import com.api.devsync.entity.Commit;
import com.api.devsync.exception.NotFoundException;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.service.CommitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommitServiceImpl implements CommitService {
    private final CommitRepository commitRepository;

    public CommitServiceImpl(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Override
    public List<Commit> get() {
        return commitRepository.findAll();
    }

    @Override
    public Commit getByHash(String hash) {
        return commitRepository.findByHash(hash)
                .orElseThrow(() -> new NotFoundException("commitNotFound"));
    }
}
