package com.careerpirates.resumate.review.docs;

import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.review.application.dto.request.ReviewListRequest;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewListResponse;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회고", description = "✍️ 회고 API - 회고 관리")
public interface ReviewControllerDocs {

    @Operation(method = "POST", summary = "회고 작성", description = "새로운 회고를 작성합니다. 즉시 완료하거나 임시 저장할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회고 작성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request);

    @Operation(method = "PUT", summary = "회고 수정", description = "회고를 수정합니다. 회고 내용과 함께 폴더와 작성 완료 여부를 변경할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 수정에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.<br>회고를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<ReviewResponse> updateReview(@PathVariable Long id, @RequestBody @Valid ReviewRequest request);

    @Operation(method = "DELETE", summary = "회고 삭제 (휴지통 보내기)", description = "회고를 휴지통으로 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 삭제에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> deleteReview(@PathVariable Long id);

    @Operation(method = "DELETE", summary = "회고 영구 삭제", description = "휴지통의 회고를 영구 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 영구 삭제에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> deleteReviewPermanently(@PathVariable Long id);

    @Operation(method = "PATCH", summary = "회고 삭제 복원", description = "휴지통의 회고를 복원합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 삭제 복원에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.<br>회고를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> restoreReview(@PathVariable Long id, @RequestParam Long folderId);

    @Operation(method = "GET", summary = "회고 상세 조회", description = "회고를 상세 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 상세 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "회고를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<ReviewResponse> getReview(@PathVariable Long id);

    @Operation(method = "GET", summary = "회고 목록 조회", description = "전체 회고 목록을 조회합니다. 임시 저장 상태인 회고와 휴지통의 회고를 조회할 수도 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회고 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<ReviewListResponse> getReviews(@ModelAttribute ReviewListRequest request);
}
