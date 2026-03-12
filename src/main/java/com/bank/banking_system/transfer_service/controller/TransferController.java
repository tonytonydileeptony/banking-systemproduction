package com.bank.banking_system.transfer_service.controller;



import com.bank.banking_system.transfer_service.dto.ApiResponse;
import com.bank.banking_system.transfer_service.dto.TransferRequest;
import com.bank.banking_system.transfer_service.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }



    @PostMapping
    public ResponseEntity<String> transfer(@Valid
            @RequestBody TransferRequest req) {

        return ResponseEntity.ok(
                transferService.transfer(req));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransferRequest>>> getTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TransferRequest> result =
                transferService.getTransactions(
                        status, minAmount,
                        startDate, endDate,
                        pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Transactions fetched successfully",
                        result
                )
        );
    }

}

