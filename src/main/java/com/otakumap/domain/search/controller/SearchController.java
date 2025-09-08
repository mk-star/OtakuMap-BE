package com.otakumap.domain.search.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.search.dto.SearchResponseDTO;
import com.otakumap.domain.search.service.SearchService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/map/search")
    @Operation(summary = "이벤트/작품명 지도 검색", description = "키워드로 이벤트/장소명/애니메이션 제목을 검색하여 관련된 장소 위치와 그 위치의 이벤트를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드입니다."),
    })
    public ApiResponse<List<SearchResponseDTO.SearchResultDTO>> getSearchedPlaceInfoList(@CurrentUser(required=false) User user, @RequestParam String keyword) {

        return ApiResponse.onSuccess(searchService.getSearchedResult(user, keyword));
    }
}
