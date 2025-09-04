package com.careerpirates.resumate.analysis.docs;

import com.careerpirates.resumate.analysis.application.dto.response.AnalysisListResponse;
import com.careerpirates.resumate.analysis.application.dto.response.AnalysisResponse;
import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "분석", description = "🧠️ 회고 분석 API - 회고 분석 요청 및 조회")
public interface AnalysisControllerDocs {

    @Operation(method = "POST", summary = "회고 분석 요청", description = "회고 분석을 요청합니다. 요청은 비동기로 처리됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 분석 요청에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> requestAnalysis(@RequestParam("folders") List<Long> folders);

    @Operation(method = "GET", summary = "분석 결과 조회", description = "회고 분석 결과를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "분석 결과 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "분석 객체를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<AnalysisResponse> getAnalysis(@PathVariable Long folderId,
                                                  @RequestParam(required = false) Long id);

    @Operation(method = "GET", summary = "분석 결과 목록 조회", description = "회고 분석 결과 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "분석 결과 목록 조회에 성공하였습니다."),
    })
    SuccessResponse<AnalysisListResponse> getAnalysisList(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size);
}
