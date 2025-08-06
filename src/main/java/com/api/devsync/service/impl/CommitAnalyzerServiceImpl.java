package com.api.devsync.service.impl;

import com.api.devsync.entity.CommitAnalysis;
import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.model.dto.AnalyzeAIDto;
import com.api.devsync.repository.CommitAnalysisRepository;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.service.CommitAnalyzerService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CommitAnalyzerServiceImpl implements CommitAnalyzerService {
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;

    public CommitAnalyzerServiceImpl(CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository) {
        this.commitRepository = commitRepository;
        this.commitAnalysisRepository = commitAnalysisRepository;
    }

    @Override
    public void applyAiAnalysisToCommits(PullRequestAnalysis analyze, AnalyzeAIDto aiResult) {

        if (aiResult.getCommitAnalyses() == null || aiResult.getCommitAnalyses().isEmpty()) {
            analyze.setCommitAnalysis(Collections.emptyList());
            return;
        }

        List<CommitAnalysis> commitAnalyses = aiResult.getCommitAnalyses().stream().map(dto -> {
            CommitAnalysis commitAnalysis = commitAnalysisRepository.findById(dto.getHash()).orElseGet(() -> {
                CommitAnalysis newCa = new CommitAnalysis();
                newCa.setId(dto.getHash());
                return newCa;
            });
            commitAnalysis.setAuthor(dto.getAuthor());
            commitAnalysis.setRiskScore(dto.getRiskScore());
            commitAnalysis.setRiskReason(dto.getRiskReason());
            commitAnalysis.setFunctionalComment(dto.getFunctionalComment());
            commitAnalysis.setArchitecturalComment(dto.getArchitecturalComment());
            commitAnalysis.setTechnicalComment(dto.getTechnicalComment());
            commitRepository.findById(dto.getHash()).ifPresent(commitAnalysis::setCommit);
            return commitAnalysis;
        }).toList();
        analyze.setCommitAnalysis(commitAnalyses);
    }
}
