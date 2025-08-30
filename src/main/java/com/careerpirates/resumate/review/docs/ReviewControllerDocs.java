package com.careerpirates.resumate.review.docs;

import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import com.careerpirates.resumate.review.application.dto.request.ReviewRequest;
import com.careerpirates.resumate.review.application.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

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
}
