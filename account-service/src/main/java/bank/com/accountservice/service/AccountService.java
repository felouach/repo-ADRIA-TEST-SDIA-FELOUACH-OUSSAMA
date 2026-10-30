package bank.com.accountservice.service;


import bank.com.accountservice.domain.Account;
import bank.com.accountservice.domain.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import bank.com.accountservice.repository.AccountRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public Account create(Account account) {
        if (account.getStatus() == null) account.setStatus(AccountStatus.ACTIVE);
        return repository.save(account);
    }

    public List<Account> findAll() {
        return repository.findAll();
    }

    public Account findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional
    public Account updateBalance(UUID id, double newBalance) {
        Account account = findById(id);
        account.setBalance(newBalance);
        return repository.save(account);
    }

    @Transactional
    public Account updateStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return repository.save(account);
    }
}