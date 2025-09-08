package com.otakumap.domain.transaction.controller;


import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.transaction.dto.TransactionResponseDTO;
import com.otakumap.domain.transaction.service.TransactionQueryService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionQueryService transactionQueryService;

    @Operation(summary = "구매 내역 확인", description = "구매 내역을 조회합니다.")
    @GetMapping("/usages")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호입니다. 0부터 시작합니다.", example = "0"),
            @Parameter(name = "size", description = "한 페이지당 내역 수", example = "5"),
    })
    public ApiResponse<TransactionResponseDTO.TransactionListDTO> getUsageTransactions(@CurrentUser User user,
                                                                                       @RequestParam(name = "page") Integer page,
                                                                                       @RequestParam(name = "size") Integer size) {
        return ApiResponse.onSuccess(transactionQueryService.getUsageTransactions(user, page, size));
    }

    @Operation(summary = "수익 내역 확인", description = "수익 내역을 조회합니다.")
    @GetMapping("/earnings")
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호입니다. 0부터 시작합니다.", example = "0"),
            @Parameter(name = "size", description = "한 페이지당 불러올 수익 내역 수", example = "5"),
    })
    public ApiResponse<TransactionResponseDTO.TransactionListDTO> getEarningTransactions(@CurrentUser User user,
                                                                                         @RequestParam(name = "page") Integer page,
                                                                                         @RequestParam(name = "size") Integer size) {

        return ApiResponse.onSuccess(transactionQueryService.getEarningTransactions(user, page, size));
    }
}
