package com.otakumap.domain.place_short_review.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.place_short_review.dto.PlaceShortReviewResponseDTO;
import com.otakumap.domain.place_short_review.converter.PlaceShortReviewConverter;
import com.otakumap.domain.place_short_review.dto.PlaceShortReviewRequestDTO;
import com.otakumap.domain.place_short_review.entity.PlaceShortReview;
import com.otakumap.domain.place_short_review.service.PlaceShortReviewCommandService;
import com.otakumap.domain.place_short_review.service.PlaceShortReviewQueryService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import com.otakumap.global.validation.annotation.ExistPlace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class PlaceShortReviewController {

    private final PlaceShortReviewQueryService placeShortReviewQueryService;
    private final PlaceShortReviewCommandService placeShortReviewCommandService;

    @GetMapping("/places/{placeId}/short-review")
    @Operation(summary = "특정 명소의 한 줄 리뷰 목록 조회", description = "특정 명소의 한 줄 리뷰 목록을 불러옵니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "placeId", description = "명소의 아이디입니다."),
            @Parameter(name = "page", description = "페이지 번호입니다. 0부터 시작합니다.", example = "0")
    })
    public ApiResponse<PlaceShortReviewResponseDTO.PlaceShortReviewListDTO> getPlaceShortReviewList(@ExistPlace @PathVariable(name = "placeId") Long placeId, @RequestParam(name = "page") Integer page){
        return ApiResponse.onSuccess(PlaceShortReviewConverter.placeShortReviewListDTO(placeShortReviewQueryService.getPlaceShortReviews(placeId, page)));
    }

    @PostMapping("/places/{placeId}/short-review")
    @Operation(summary = "특정 명소의 한 줄 리뷰 작성 API", description = "특정 명소의 한 줄 리뷰를 작성하는 API입니다. PlaceAnimationId는 특정 명소 관련 애니메이션 조회 API를 통해 얻을 수 있습니다.")
    public ApiResponse<PlaceShortReviewResponseDTO.CreateReviewDTO> createReview(
            @CurrentUser User user,
            @PathVariable Long placeId,
            @RequestBody @Valid PlaceShortReviewRequestDTO.CreateDTO request) {
        PlaceShortReview placeShortReview = placeShortReviewCommandService.createReview(user, placeId, request);
        return ApiResponse.onSuccess(PlaceShortReviewConverter.toCreateReviewDTO(placeShortReview));
    }

    @Operation(summary = "명소 한 줄 리뷰 수정", description = "명소 한 줄 리뷰의 별점과 내용을 수정합니다.")
    @PatchMapping("/places/short-reviews/{placeShortReviewId}")
    @Parameters({
            @Parameter(name = "placeShortReviewId", description = "특정 한 줄 리뷰의 Id")
    })
    public ApiResponse<String> updatePlaceShortReview(@PathVariable Long placeShortReviewId, @RequestBody @Valid PlaceShortReviewRequestDTO.UpdatePlaceShortReviewDTO request) {
        placeShortReviewCommandService.updatePlaceShortReview(placeShortReviewId, request);
        return ApiResponse.onSuccess("한 줄 리뷰가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "명소 한 줄 리뷰 삭제", description = "명소 한 줄 리뷰를 삭제합니다.")
    @DeleteMapping("/places/short-reviews/{placeShortReviewId}")
    @Parameters({
            @Parameter(name = "placeShortReviewId", description = "특정 한 줄 리뷰의 Id")
    })
    public ApiResponse<String> deletePlaceShortReview(@PathVariable Long placeShortReviewId) {
        placeShortReviewCommandService.deletePlaceShortReview(placeShortReviewId);
        return ApiResponse.onSuccess("한 줄 리뷰가 성공적으로 삭제되었습니다.");
    }
}
