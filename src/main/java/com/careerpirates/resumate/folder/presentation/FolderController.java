package com.careerpirates.resumate.folder.presentation;

import com.careerpirates.resumate.folder.application.dto.request.FolderNameRequest;
import com.careerpirates.resumate.folder.application.dto.request.FolderRequest;
import com.careerpirates.resumate.folder.application.dto.response.FolderResponse;
import com.careerpirates.resumate.folder.application.service.FolderService;
import com.careerpirates.resumate.folder.message.success.FolderSuccess;
import com.careerpirates.resumate.global.message.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public SuccessResponse<FolderResponse> createFolder(@RequestBody @Valid FolderRequest request) {

        FolderResponse response = folderService.createFolder(request);
        return SuccessResponse.of(FolderSuccess.FOLDER_CREATE_SUCCESS, response);
    }

    @PatchMapping("/{id}")
    public SuccessResponse<FolderResponse> updateFolderName(@PathVariable Long id,
                                                            @RequestBody @Valid FolderNameRequest request) {
        FolderResponse response = folderService.updateFolderName(id, request);
        return SuccessResponse.of(FolderSuccess.FOLDER_UPDATE_NAME_SUCCESS, response);
    }
}
