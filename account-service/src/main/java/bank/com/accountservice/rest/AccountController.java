package bank.com.accountservice.rest;


import bank.com.accountservice.domain.Account;
import bank.com.accountservice.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import bank.com.accountservice.service.AccountService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        return service.create(account);
    }

    @GetMapping
    public List<Account> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}/balance")
    public Account updateBalance(@PathVariable UUID id, @RequestBody Map<String, Double> body) {
        return service.updateBalance(id, body.get("balance"));
    }

    @PutMapping("/{id}/status")
    public Account updateStatus(@PathVariable UUID id, @RequestParam AccountStatus status) {
        return service.updateStatus(id, status);
    }
}
