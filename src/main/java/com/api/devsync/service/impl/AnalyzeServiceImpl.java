package com.api.devsync.service.impl;

import com.api.devsync.entity.Analyze;
import com.api.devsync.entity.CommitAnalysis;
import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.mapper.AnalyzeMapper;
import com.api.devsync.mapper.PullRequestAnalyzeMapper;
import com.api.devsync.mapper.custom.CustomPullRequestAnalyzeMapper;
import com.api.devsync.model.dto.AnalyzeAIDto;
import com.api.devsync.model.dto.AnalyzeDto;
import com.api.devsync.model.dto.PullRequestAnalysisDto;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.api.devsync.repository.PullRequestAnalysisRepository;
import com.api.devsync.service.AnalyzeService;
import com.api.devsync.service.PullRequestAnalyzerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {
    private final PullRequestAnalysisRepository repository;
    private final AnalyzeMapper analyzeMapper;
    private final PullRequestAnalyzerService pullRequestAnalyzerService;
    private final CustomPullRequestAnalyzeMapper customPullRequestAnalyzeMapper;
    private final PullRequestAnalyzeMapper pullRequestAnalyzeMapper;

    public AnalyzeServiceImpl(PullRequestAnalysisRepository repository, AnalyzeMapper analyzeMapper, PullRequestAnalyzerService pullRequestAnalyzerService, CustomPullRequestAnalyzeMapper customPullRequestAnalyzeMapper, PullRequestAnalyzeMapper pullRequestAnalyzeMapper) {
        this.repository = repository;
        this.analyzeMapper = analyzeMapper;
        this.pullRequestAnalyzerService = pullRequestAnalyzerService;
        this.customPullRequestAnalyzeMapper = customPullRequestAnalyzeMapper;
        this.pullRequestAnalyzeMapper = pullRequestAnalyzeMapper;
    }

    public List<AnalyzeDto> get(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PullRequestAnalysis> analyzes = repository.findAll(pageable);
        return analyzes.stream().map(analyzeMapper::toDto).toList();
    }

    public List<AnalyzeDto> getByRepository(Long repoId) {
        List<PullRequestAnalysis> analyzes = repository.findByPullRequest_Repository_Id(repoId);
        return analyzes.stream().map(analyzeMapper::toDto).toList();
    }

    public AnalyzeDto getById(UUID id) {
        Analyze analyze = repository.findById(id).orElseThrow(NullPointerException::new);
        return analyzeMapper.toDto(analyze);
    }

    public AnalyzeDto getByPullRequest(Long pullRequestId) {
        Analyze analyze = repository.findByPullRequestId(pullRequestId).orElseThrow(NullPointerException::new);
        return analyzeMapper.toDto(analyze);
    }


    @Transactional
    public PullRequestAnalysisDto createAnalyze(GithubWebhookModel model) throws JsonProcessingException {
        PullRequestAnalysis analyze = customPullRequestAnalyzeMapper.mapFromDto(model);
        getAnalyzeFromAI(analyze, model);
        PullRequestAnalysis createdAnalyze = repository.save(analyze);
        return pullRequestAnalyzeMapper.toDto(createdAnalyze);
    }

    private void getAnalyzeFromAI(PullRequestAnalysis analyze, GithubWebhookModel model) throws JsonProcessingException {
        AnalyzeAIDto analyzedPullRequest = pullRequestAnalyzerService.analyze(model);
        analyze.setTechnicalComment(analyzedPullRequest.getPullRequestAnalysis().getTechnicalComment());
        analyze.setFunctionalComment(analyzedPullRequest.getPullRequestAnalysis().getTechnicalComment());
        analyze.setArchitecturalComment(analyzedPullRequest.getPullRequestAnalysis().getTechnicalComment());
        analyze.setCommitAnalysis(analyzedPullRequest.getCommitAnalyses().stream().map(c -> {
            CommitAnalysis newAnalyze = new CommitAnalysis();
            newAnalyze.setId(c.getHash());
            newAnalyze.setFunctionalComment(c.getFunctionalComment());
            newAnalyze.setArchitecturalComment(c.getArchitecturalComment());
            newAnalyze.setTechnicalComment(c.getTechnicalComment());
            return newAnalyze;
        }).toList());
    }



}
