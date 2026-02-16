package com.bank.banking_system.transfer.controller;



import com.bank.banking_system.transfer.dto.TransferRequest;
import com.bank.banking_system.transfer.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }



    @PostMapping
    public ResponseEntity<String> transfer(
            @RequestBody TransferRequest req) {

        return ResponseEntity.ok(
                transferService.transfer(req));
    }

}

