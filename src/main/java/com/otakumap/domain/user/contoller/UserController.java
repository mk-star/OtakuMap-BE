package com.otakumap.domain.user.contoller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.event_review.service.EventReviewCommandService;
import com.otakumap.domain.place_review.service.PlaceReviewCommandService;
import com.otakumap.domain.user.converter.UserConverter;
import com.otakumap.domain.user.dto.UserRequestDTO;
import com.otakumap.domain.user.dto.UserResponseDTO;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.service.UserCommandService;
import com.otakumap.domain.user.service.UserQueryService;
import com.otakumap.global.apiPayload.ApiResponse;
import com.otakumap.global.validation.annotation.CheckPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final PlaceReviewCommandService placeReviewCommandService;
    private final EventReviewCommandService eventReviewCommandService;

    @GetMapping
    @Operation(summary = "회원 정보 조회 API", description = "회원 정보를 조회합니다.")
    public ApiResponse<UserResponseDTO.UserInfoResponseDTO> getUserInfo(@CurrentUser User user) {
        User userInfo = userQueryService.getUserInfo(user.getId());
        return ApiResponse.onSuccess(UserConverter.toUserInfoResponseDTO(userInfo));
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경 API", description = "회원의 닉네임을 변경합니다.")
    public ApiResponse<String> updateNickname(
            @CurrentUser User user,
            @Valid @RequestBody UserRequestDTO.UpdateNicknameDTO request) {
        userCommandService.updateNickname(user, request);
        return ApiResponse.onSuccess("닉네임이 성공적으로 수정되었습니다.");
    }

    @PostMapping("/report-event")
    @Operation(summary = "이벤트 제보 API", description = "이벤트를 제보합니다.")
    public ApiResponse<String> reportEvent(
            @Valid @RequestBody UserRequestDTO.UserReportRequestDTO request) {
        userCommandService.reportEvent(request);
        return ApiResponse.onSuccess("이벤트 제보가 성공적으로 전송되었습니다.");
    }

    @PatchMapping("/notification-settings")
    @Operation(summary = "알림 설정 변경 API", description = "회원의 알림 설정을 변경합니다.")
    public ApiResponse<String> updateNotificationSettings(
            @CurrentUser User user,
            @Valid @RequestBody UserRequestDTO.NotificationSettingsRequestDTO request) {
        userCommandService.updateNotificationSettings(user, request);
        return ApiResponse.onSuccess("알림 설정이 성공적으로 업데이트되었습니다.");
    }

    @GetMapping("/my-reviews")
    @Operation(summary = "내가 작성한 리뷰 조회 API", description = "내가 작성한 리뷰를 조회합니다.")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 쿼리스트링입니다! (1~)"),
            @Parameter(name = "sort", description = "정렬 기준 쿼리스트링입니다! (createdAt, views)")
    })
    public ApiResponse<UserResponseDTO.UserReviewListDTO> getMyReviews(
            @CurrentUser User user, @CheckPage @RequestParam(name = "page") Integer page,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort) {
        Page<UserResponseDTO.UserReviewDTO> reviews = userQueryService.getMyReviews(user, page, sort);
        return ApiResponse.onSuccess(UserConverter.reviewListDTO(reviews));
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid UserRequestDTO.ResetPasswordDTO request) {
        userCommandService.resetPassword(request);
        return ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다.");
    }

    @DeleteMapping("/reviews")
    @Operation(summary = "내가 작성한 후기 삭제", description = "내가 작성한 모든 후기를 삭제합니다.")
    public ApiResponse<String> deleteAllReviews(@CurrentUser User user) {
        placeReviewCommandService.deleteAllByUserId(user.getId());
        eventReviewCommandService.deleteAllByUserId(user.getId());
        return ApiResponse.onSuccess("모든 후기가 성공적으로 삭제되었습니다.");
    }

    @PatchMapping(value = "/profile_image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지를 변경합니다.")
    public ApiResponse<String> updateProfileImage(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @CurrentUser User user,
            @RequestPart("profileImage") MultipartFile profileImage) {
        String profileImageUrl = userCommandService.updateProfileImage(user, profileImage);
        return ApiResponse.onSuccess("프로필 이미지가 성공적으로 변경되었습니다. URL: " + profileImageUrl);
    }

    @PatchMapping("/email")
    @Operation(summary = "이메일 변경 API", description = "이메일 변경을 위한 기능입니다. 중복 확인 및 인증 API를 먼저 사용한 후 이용해주세요.")
    public ApiResponse<String> changeEmail(@RequestBody @Valid UserRequestDTO.ChangeEmailDTO request, @CurrentUser User user) {
        userCommandService.changeEmail(user, request);
        return ApiResponse.onSuccess("이메일 변경이 성공적으로 완료되었습니다.");
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경 API", description = "비밀번호 변경을 위한 기능입니다.")
    public ApiResponse<String> changePassword(@RequestBody @Valid UserRequestDTO.ChangePasswordDTO request, @CurrentUser User user) {
        userCommandService.changePassword(user, request);
        return ApiResponse.onSuccess("비밀번호 변경이 성공적으로 완료되었습니다.");
    }
}
