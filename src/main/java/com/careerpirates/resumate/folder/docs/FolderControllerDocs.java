package com.careerpirates.resumate.folder.docs;

import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.application.dto.response.FolderTreeResponse;
import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "폴더", description = "📁 폴더 API - 폴더 관리")
public interface FolderControllerDocs {

    @Operation(method = "POST", summary = "폴더 추가", description = "상위 폴더나 어떤 폴더의 하위 폴더로 새로운 폴더를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "폴더 추가에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "폴더 이름이 유효하지 않습니다. (빈 문자열, 50자 초과)<br>폴더 표시 순서는 0 미만일 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "상위 폴더를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "자기 자신이나 자식을 상위 폴더로 설정할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<FolderResponse> createFolder(@RequestBody @Valid FolderRequest request);

    @Operation(method = "PATCH", summary = "폴더 이름 변경", description = "폴더의 이름을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 이름 변경에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "폴더 이름이 유효하지 않습니다. (빈 문자열, 50자 초과)<br>폴더 표시 순서는 0 미만일 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<FolderResponse> updateFolderName(@PathVariable Long id,
                                                     @RequestBody @Valid FolderNameRequest request);

    @Operation(method = "DELETE", summary = "폴더 삭제", description = "폴더를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 삭제에 성공하였습니다."),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    SuccessResponse<?> deleteFolder(@PathVariable Long id);

    @Operation(method = "GET", summary = "폴더 목록 조회", description = "전체 폴더의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 목록 조회에 성공하였습니다."),
    })
    SuccessResponse<List<FolderTreeResponse>> getFolders();
}
