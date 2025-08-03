package com.api.devsync.service.impl;

import com.api.devsync.entity.Analyze;
import com.api.devsync.entity.Commit;
import com.api.devsync.entity.CommitAnalysis;
import com.api.devsync.entity.PullRequestAnalysis;
import com.api.devsync.mapper.AnalyzeMapper;
import com.api.devsync.mapper.PullRequestAnalyzeMapper;
import com.api.devsync.mapper.custom.CustomPullRequestAnalyzeMapper;
import com.api.devsync.model.dto.*;
import com.api.devsync.model.fromWebhook.GithubWebhookModel;
import com.api.devsync.repository.CommitAnalysisRepository;
import com.api.devsync.repository.CommitRepository;
import com.api.devsync.repository.PullRequestAnalysisRepository;
import com.api.devsync.service.AnalyzeService;
import com.api.devsync.service.PullRequestAnalyzerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnalyzeServiceImpl implements AnalyzeService {
    private final PullRequestAnalysisRepository repository;
    private final AnalyzeMapper analyzeMapper;
    private final PullRequestAnalyzerService pullRequestAnalyzerService;
    private final CustomPullRequestAnalyzeMapper customPullRequestAnalyzeMapper;
    private final PullRequestAnalyzeMapper pullRequestAnalyzeMapper;
    private final CommitRepository commitRepository;
    private final CommitAnalysisRepository commitAnalysisRepository;
    private final PullRequestAnalyzerServiceServiceImpl pullRequestAnalyzerServiceServiceImpl;

    public AnalyzeServiceImpl(PullRequestAnalysisRepository repository, AnalyzeMapper analyzeMapper, PullRequestAnalyzerService pullRequestAnalyzerService, CustomPullRequestAnalyzeMapper customPullRequestAnalyzeMapper, PullRequestAnalyzeMapper pullRequestAnalyzeMapper, CommitRepository commitRepository, CommitAnalysisRepository commitAnalysisRepository, PullRequestAnalyzerServiceServiceImpl pullRequestAnalyzerServiceServiceImpl) {
        this.repository = repository;
        this.analyzeMapper = analyzeMapper;
        this.pullRequestAnalyzerService = pullRequestAnalyzerService;
        this.customPullRequestAnalyzeMapper = customPullRequestAnalyzeMapper;
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


    public PullRequestAnalysisDto createAnalyze(GithubWebhookModel model) throws JsonProcessingException {
        PullRequestAnalysis analyze = customPullRequestAnalyzeMapper.mapFromDto(model);
        getAnalyzeFromAI(analyze, model);
        return pullRequestAnalyzeMapper.toDto(analyze);
    }

    private void getAnalyzeFromAI(PullRequestAnalysis analyze, GithubWebhookModel model) throws JsonProcessingException {
        AnalyzeAIDto analyzedPullRequest = pullRequestAnalyzerServiceServiceImpl.analyze(model);

        analyze.setTechnicalComment(analyzedPullRequest.getPullRequestAnalysis().getTechnicalComment());
        analyze.setFunctionalComment(analyzedPullRequest.getPullRequestAnalysis().getFunctionalComment());
        analyze.setArchitecturalComment(analyzedPullRequest.getPullRequestAnalysis().getArchitecturalComment());

        List<CommitAnalysis> commitAnalyses = analyzedPullRequest.getCommitAnalyses().stream().map(c -> {
            CommitAnalysis ca = commitAnalysisRepository.findById(c.getHash())
                    .orElseGet(() -> {
                        CommitAnalysis newCa = new CommitAnalysis();
                        newCa.setId(UUID.randomUUID().toString());
                        return newCa;
                    });

            ca.setRiskScore(c.getRiskScore());
            ca.setFunctionalComment(c.getFunctionalComment());
            ca.setArchitecturalComment(c.getArchitecturalComment());
            ca.setTechnicalComment(c.getTechnicalComment());

            Commit commit = commitRepository.findById(c.getHash()).orElse(null);
            ca.setCommit(commit);

            return ca;
        }).toList();

        analyze.setCommitAnalysis(commitAnalyses);
    }


    private AnalyzeAIDto getMockAnalyzeAIDto(GithubWebhookModel model) {
        PullRequestAIAnalyzeDto prAnalysis = new PullRequestAIAnalyzeDto();
        prAnalysis.setRiskScore(75);
        prAnalysis.setTechnicalComment("Kod düzeni iyi, ancak dependency injection kısmında iyileştirme yapılabilir.");
        prAnalysis.setFunctionalComment("İşlevsel gereksinimler karşılanıyor fakat edge case testleri eksik.");
        prAnalysis.setArchitecturalComment("Katmanlı mimari prensiplerine genel olarak uyulmuş.");

        List<CommitAIAnalyzeDto> commitAnalyses = new ArrayList<>();

        CommitAIAnalyzeDto commit1 = new CommitAIAnalyzeDto();
        commit1.setHash("abc123");
        commit1.setMessage("Refactored user service");
        commit1.setTechnicalComment("Method extraction yapılmış, clean code açısından iyi bir adım.");
        commit1.setFunctionalComment("Yeni metot mevcut fonksiyonları bozmadı.");
        commit1.setArchitecturalComment("Service katmanı controller bağımlılığını azaltmış.");
        commit1.setRiskScore(30);
        commitAnalyses.add(commit1);

        CommitAIAnalyzeDto commit2 = new CommitAIAnalyzeDto();
        commit2.setHash("def456");
        commit2.setMessage("Fixed bug in authentication flow");
        commit2.setTechnicalComment("Null kontrolü eklenmiş.");
        commit2.setFunctionalComment("Kritik hata giderildi, login sorunsuz çalışıyor.");
        commit2.setArchitecturalComment("Auth modülünde ufak çaplı bağımsızlık sağlanmış.");
        commit2.setRiskScore(20);
        commitAnalyses.add(commit2);

        CommitAIAnalyzeDto commit3 = new CommitAIAnalyzeDto();
        commit3.setHash("ghi789");
        commit3.setMessage("Added caching for repository calls");
        commit3.setTechnicalComment("Cache eklenmesi performansı artıracak.");
        commit3.setFunctionalComment("Data consistency sorunu yaşanabilir, TTL kontrol edilmeli.");
        commit3.setArchitecturalComment("Caching yapısı servis katmanına uygun konumlandırılmış.");
        commit3.setRiskScore(50);
        commitAnalyses.add(commit3);

        // DTO'yu birleştir
        AnalyzeAIDto analyzeAIDto = new AnalyzeAIDto();
        analyzeAIDto.setPullRequestAnalysis(prAnalysis);
        analyzeAIDto.setCommitAnalyses(commitAnalyses);

        return analyzeAIDto;
    }




}
