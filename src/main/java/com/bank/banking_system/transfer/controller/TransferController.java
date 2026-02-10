package com.bank.banking_system.transfer.controller;



import com.bank.banking_system.transfer.service.TransferRequest;
import com.bank.banking_system.transfer.service.TransferService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public String transfer(@RequestBody TransferRequest request) {
        transferService.transfer(request);
        return "Transfer Successful";
    }
}

