package com.careerpirates.resumate.folder.presentation;

import com.careerpirates.resumate.auth.application.dto.CustomMemberDetails;
import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderOrderRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.application.dto.response.FolderTreeResponse;
import com.careerpirates.resumate.folder.application.service.FolderService;
import com.careerpirates.resumate.folder.docs.FolderControllerDocs;
import com.careerpirates.resumate.folder.message.success.FolderSuccess;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
public class FolderController implements FolderControllerDocs {

    private final FolderService folderService;

    @PostMapping
    public SuccessResponse<FolderResponse> createFolder(@RequestBody @Valid FolderRequest request,
                                                        @AuthenticationPrincipal CustomMemberDetails member) {

        FolderResponse response = folderService.createFolder(request, member.getMemberId());
        return SuccessResponse.of(FolderSuccess.CREATE_FOLDER, response);
    }

    @PatchMapping("/{id}")
    public SuccessResponse<FolderResponse> updateFolderName(@PathVariable Long id,
                                                            @RequestBody @Valid FolderNameRequest request,
                                                            @AuthenticationPrincipal CustomMemberDetails member) {
        FolderResponse response = folderService.updateFolderName(id, request, member.getMemberId());
        return SuccessResponse.of(FolderSuccess.UPDATE_FOLDER_NAME, response);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<?> deleteFolder(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails member) {

        folderService.deleteFolder(id, member.getMemberId());
        return SuccessResponse.of(FolderSuccess.DELETE_FOLDER);
    }

    @GetMapping
    public SuccessResponse<List<FolderTreeResponse>> getFolders(@AuthenticationPrincipal CustomMemberDetails member) {
        List<FolderTreeResponse> response = folderService.getFolders(member.getMemberId());
        return SuccessResponse.of(FolderSuccess.GET_FOLDERS, response);
    }

    @PatchMapping("/tree")
    public SuccessResponse<List<FolderTreeResponse>> setFolderOrder(@RequestBody @Valid List<FolderOrderRequest> request,
                                                                    @AuthenticationPrincipal CustomMemberDetails member) {
        List<FolderTreeResponse> response = folderService.setFolderOrder(request, member.getMemberId());
        return SuccessResponse.of(FolderSuccess.SET_FOLDER_ORDER, response);
    }

    @PatchMapping("/{id}/tree")
    public SuccessResponse<List<FolderTreeResponse>> setSubFolderTree(@PathVariable Long id,
                                                                      @RequestBody @Valid List<FolderOrderRequest> request,
                                                                      @AuthenticationPrincipal CustomMemberDetails member) {
        List<FolderTreeResponse> response = folderService.setSubFolderTree(id, request, member.getMemberId());
        return SuccessResponse.of(FolderSuccess.SET_SUB_FOLDER_TREE, response);
    }
}
