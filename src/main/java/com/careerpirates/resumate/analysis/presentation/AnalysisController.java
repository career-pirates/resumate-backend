package com.careerpirates.resumate.analysis.presentation;

import com.careerpirates.resumate.analysis.application.dto.response.AnalysisListResponse;
import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResponse;
import com.careerpirates.resumate.analysis.application.service.AnalysisService;
import com.careerpirates.resumate.analysis.docs.AnalysisControllerDocs;
import com.careerpirates.resumate.analysis.message.exception.AnalysisError;
import com.careerpirates.resumate.analysis.message.success.AnalysisSuccess;
import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{folderId}")
    public SuccessResponse<AnalysisResponse> getAnalysis(@PathVariable Long folderId) {

        AnalysisResponse response = analysisService.getAnalysis(folderId);
        return SuccessResponse.of(AnalysisSuccess.GET_ANALYSIS, response);
    }

    @GetMapping
    public SuccessResponse<AnalysisListResponse> getAnalysisList(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {

        AnalysisListResponse response = analysisService.getAnalysisList(page, size);
        return SuccessResponse.of(AnalysisSuccess.GET_ANALYSIS_LIST, response);
    }
}
