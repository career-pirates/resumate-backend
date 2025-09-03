package com.careerpirates.resumate.analysis.presentation;

import com.careerpirates.resumate.analysis.application.service.AnalysisService;
import com.careerpirates.resumate.analysis.docs.AnalysisControllerDocs;
import com.careerpirates.resumate.analysis.message.exception.AnalysisError;
import com.careerpirates.resumate.analysis.message.success.AnalysisSuccess;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController implements AnalysisControllerDocs {

    private final AnalysisService analysisService;

    @PostMapping
    public SuccessResponse<?> requestAnalysis(@RequestParam("folders") List<Long> folders) {
        if (folders.isEmpty()) {
            throw new BusinessException(AnalysisError.NO_FOLDER_REQUESTED);
        }

        for (Long folderId : folders) {
            analysisService.requestAnalysis(folderId);
        }

        return SuccessResponse.of(AnalysisSuccess.REQUEST_ANALYSIS);
    }
}
