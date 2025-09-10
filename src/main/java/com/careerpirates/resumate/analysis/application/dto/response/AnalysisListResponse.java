package com.careerpirates.resumate.analysis.application.dto.response;

import com.careerpirates.resumate.analysis.domain.Analysis;
import com.careerpirates.resumate.folder.domain.Folder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class AnalysisListResponse {

    private List<AnalysisSimpleResponse> analysis;
    private int page;
    private int size;
    private boolean hasNext;

    public static AnalysisListResponse of(Slice<Analysis> analysisSlice, Map<Long, Folder> folderMap) {
        List<AnalysisSimpleResponse> analysis = analysisSlice.getContent().stream()
                .map(a -> AnalysisSimpleResponse.of(a, folderMap.get(a.getFolderId())))
                .toList();

        return AnalysisListResponse.builder()
                .analysis(analysis)
                .page(analysisSlice.getNumber())
                .size(analysisSlice.getSize())
                .hasNext(analysisSlice.hasNext())
                .build();
    }
}
