package com.api.devsync.service.impl;

import com.api.devsync.entity.Analyze;
import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.mapper.AnalyzeMapper;
import com.api.devsync.mapper.PullRequestAnalyzeMapper;
import com.api.devsync.model.dto.*;
import com.api.devsync.model.viewmodel.fromApi.commit.FileChangeFromApi;
import com.api.devsync.repository.PullRequestAnalysisRepository;
import com.api.devsync.service.AnalyzeService;
import com.api.devsync.service.CommitAnalyzerService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final PullRequestAnalyzeMapper pullRequestAnalyzeMapper;
    private final PullRequestAnalyzerServiceImpl pullRequestAnalyzerServiceImpl;
    private final CommitAnalyzerService commitAnalyzerService;

    public AnalyzeServiceImpl(PullRequestAnalysisRepository repository, AnalyzeMapper analyzeMapper, PullRequestAnalyzeMapper pullRequestAnalyzeMapper, PullRequestAnalyzerServiceImpl pullRequestAnalyzerServiceImpl, CommitAnalyzerService commitAnalyzerService) {
        this.repository = repository;
        this.analyzeMapper = analyzeMapper;
        this.pullRequestAnalyzeMapper = pullRequestAnalyzeMapper;
        this.pullRequestAnalyzerServiceImpl = pullRequestAnalyzerServiceImpl;
        this.commitAnalyzerService = commitAnalyzerService;
    }

    @Override
    public List<AnalyzeDto> get(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PullRequestAnalysis> analyzes = repository.findAll(pageable);
        return analyzes.stream().map(analyzeMapper::toDto).toList();
    }

    @Override
    public List<AnalyzeDto> getByRepository(Long repoId) {
        List<PullRequestAnalysis> analyzes = repository.findByPullRequest_Repository_Id(repoId);
        return analyzes.stream().map(analyzeMapper::toDto).toList();
    }

    @Override
    public AnalyzeDto getById(UUID id) {
        Analyze analyze = repository.findById(id).orElseThrow(NullPointerException::new);
        return analyzeMapper.toDto(analyze);
    }

    @Override
    public AnalyzeDto getByPullRequest(Long pullRequestId) {
        Analyze analyze = repository.findByPullRequestId(pullRequestId).orElseThrow(NullPointerException::new);
        return analyzeMapper.toDto(analyze);
    }

    @Override
    public PullRequestAnalysisDto createAnalyze(PrepareAnalyzeDto model) throws JsonProcessingException {
        PullRequestAnalysis analyze = buildBaseAnalysis(model);
        AnalyzeAIDto aiResult = pullRequestAnalyzerServiceImpl.analyze(model);
        applyAiAnalysisToPullRequest(analyze, aiResult);
        commitAnalyzerService.applyAiAnalysisToCommits(analyze, aiResult);
        return pullRequestAnalyzeMapper.toDto(analyze);
    }

    private PullRequestAnalysis buildBaseAnalysis(PrepareAnalyzeDto dto) {
        PullRequestAnalysis analysis = new PullRequestAnalysis();
        analysis.setTotalAdditions(dto.getCommits().stream().flatMap(a -> a.getFiles().stream()).mapToInt(FileChangeFromApi::getDeletions).sum());
        analysis.setTotalDeletions(dto.getCommits().stream().flatMap(a -> a.getFiles().stream()).mapToInt(FileChangeFromApi::getDeletions).sum());
        analysis.setBranch(dto.getBranchName());
        analysis.setRepoName(dto.getRepositoryName());
        analysis.setRepoId(dto.getRepoId());
        return analysis;
    }

    private void applyAiAnalysisToPullRequest(PullRequestAnalysis analyze, AnalyzeAIDto aiResult) {
        var prAnalysis = aiResult.getPullRequestAnalysis();
        analyze.setTechnicalComment(prAnalysis.getTechnicalComment());
        analyze.setFunctionalComment(prAnalysis.getFunctionalComment());
        analyze.setArchitecturalComment(prAnalysis.getArchitecturalComment());
        analyze.setRiskScore(prAnalysis.getRiskScore());
        analyze.setRiskReason(prAnalysis.getRiskReason());
    }






}
