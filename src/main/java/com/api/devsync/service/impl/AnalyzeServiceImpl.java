package com.api.devsync.service.impl;

import com.api.devsync.entity.Analyze;
import com.api.devsync.entity.CommitAnalysis;
import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.mapper.AnalyzeMapper;
import com.api.devsync.mapper.PullRequestAnalyzeMapper;
import com.api.devsync.model.dto.*;
import com.api.devsync.model.fromApi.commit.FileChangeFromApi;
import com.api.devsync.repository.CommitAnalysisRepository;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.repository.PullRequestAnalysisRepository;
import com.api.devsync.service.AnalyzeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {
    private final PullRequestAnalysisRepository repository;
    private final AnalyzeMapper analyzeMapper;
    private final PullRequestAnalyzeMapper pullRequestAnalyzeMapper;
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;
    private final PullRequestAnalyzerServiceServiceImpl pullRequestAnalyzerServiceServiceImpl;

    public AnalyzeServiceImpl(PullRequestAnalysisRepository repository, AnalyzeMapper analyzeMapper, PullRequestAnalyzeMapper pullRequestAnalyzeMapper, CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository, PullRequestAnalyzerServiceServiceImpl pullRequestAnalyzerServiceServiceImpl) {
        this.repository = repository;
        this.analyzeMapper = analyzeMapper;
        this.pullRequestAnalyzeMapper = pullRequestAnalyzeMapper;
        this.commitRepository = commitRepository;
        this.commitAnalysisRepository = commitAnalysisRepository;
        this.pullRequestAnalyzerServiceServiceImpl = pullRequestAnalyzerServiceServiceImpl;
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


    public PullRequestAnalysisDto createAnalyze(PrepareAnalyzeDto model) throws JsonProcessingException {
        PullRequestAnalysis analyze = buildBaseAnalysis(model);
        AnalyzeAIDto aiResult = pullRequestAnalyzerServiceServiceImpl.analyze(model);

        applyAiAnalysisToPullRequest(analyze, aiResult);
        applyAiAnalysisToCommits(analyze, aiResult);

        return pullRequestAnalyzeMapper.toDto(analyze);
    }

    private PullRequestAnalysis buildBaseAnalysis(PrepareAnalyzeDto dto) {
        PullRequestAnalysis analysis = new PullRequestAnalysis();
        analysis.setTotalAdditions(
                dto.getCommits().stream().flatMap(a -> a.getFiles().stream())
                        .mapToInt(FileChangeFromApi::getDeletions)
                        .sum()
        );
        analysis.setTotalDeletions(
                dto.getCommits().stream().flatMap(a -> a.getFiles().stream())
                        .mapToInt(FileChangeFromApi::getDeletions)
                        .sum()
        );
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
    }

    private void applyAiAnalysisToCommits(PullRequestAnalysis analyze, AnalyzeAIDto aiResult) {

        if (aiResult.getCommitAnalyses() == null || aiResult.getCommitAnalyses().isEmpty()) {
            analyze.setCommitAnalysis(Collections.emptyList());
            return;
        }

        List<CommitAnalysis> commitAnalyses = aiResult.getCommitAnalyses().stream()
                .map(dto -> {
                    CommitAnalysis commitAnalysis = commitAnalysisRepository.findById(dto.getHash())
                            .orElseGet(() -> {
                                CommitAnalysis newCa = new CommitAnalysis();
                                newCa.setId(dto.getHash());
                                return newCa;
                            });
                    commitAnalysis.setAuthor(dto.getAuthor());
                    commitAnalysis.setRiskScore(dto.getRiskScore());
                    commitAnalysis.setFunctionalComment(dto.getFunctionalComment());
                    commitAnalysis.setArchitecturalComment(dto.getArchitecturalComment());
                    commitAnalysis.setTechnicalComment(dto.getTechnicalComment());


                    commitRepository.findById(dto.getHash())
                            .ifPresent(commitAnalysis::setCommit);

                    return commitAnalysis;
                })
                .toList();

        analyze.setCommitAnalysis(commitAnalyses);
    }





}
