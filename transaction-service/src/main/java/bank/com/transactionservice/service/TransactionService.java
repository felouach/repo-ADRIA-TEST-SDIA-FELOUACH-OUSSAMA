package bank.com.transactionservice.service;

import bank.com.transactionservice.client.AccountClient;
import bank.com.transactionservice.domain.Transaction;
import bank.com.transactionservice.dto.*;
import bank.com.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final AccountClient accountClient;

    @Value("${bank.transaction.max-amount}")
    private BigDecimal maxAmount;

    @Transactional
    public Transaction create(CreateTransactionRequest req) {
        checkAmount(req.amount());

        AccountDto account = accountClient.getAccount(req.accountId());
        checkAccount(account, req.currency());

        BigDecimal balance = BigDecimal.valueOf(account.balance());
        if (balance.compareTo(req.amount()) < 0) {
            throw new BusinessException("Solde insuffisant");
        }

        Transaction saved = repository.save(Transaction.builder()
                .accountId(req.accountId())
                .amount(req.amount())
                .currency(req.currency())
                .merchantCategory(req.merchantCategory())
                .country(req.country())
                .build());

        accountClient.updateBalance(account.id(),
                new BalanceUpdate(balance.subtract(req.amount()).doubleValue()));
        return saved;
    }

    @Transactional
    public Transaction transfer(TransferRequest req) {
        checkAmount(req.amount());
        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new BusinessException("Les comptes source et destination doivent être différents");
        }

        AccountDto from = accountClient.getAccount(req.fromAccountId());
        AccountDto to = accountClient.getAccount(req.toAccountId());
        checkAccount(from, req.currency());
        checkAccount(to, req.currency());

        BigDecimal fromBalance = BigDecimal.valueOf(from.balance());
        if (fromBalance.compareTo(req.amount()) < 0) {
            throw new BusinessException("Solde insuffisant");
        }

        Transaction saved = repository.save(Transaction.builder()
                .accountId(from.id())
                .amount(req.amount())
                .currency(req.currency())
                .merchantCategory("TRANSFER")
                .build());

        accountClient.updateBalance(from.id(),
                new BalanceUpdate(fromBalance.subtract(req.amount()).doubleValue()));
        accountClient.updateBalance(to.id(),
                new BalanceUpdate(BigDecimal.valueOf(to.balance()).add(req.amount()).doubleValue()));
        return saved;
    }

    public List<Transaction> findAll() {
        return repository.findAll();
    }

    public Transaction findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction introuvable : " + id));
    }

    public List<Transaction> findByAccount(UUID accountId) {
        return repository.findByAccountId(accountId);
    }

    private void checkAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessException("Le montant doit être strictement positif");
        }
        if (amount.compareTo(maxAmount) > 0) {
            throw new BusinessException("Le montant dépasse le plafond autorisé : " + maxAmount);
        }
    }

    private void checkAccount(AccountDto account, String currency) {
        if (!"ACTIVE".equals(account.status())) {
            throw new BusinessException("Le compte " + account.id() + " n'est pas actif");
        }
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new BusinessException("La devise ne correspond pas à celle du compte " + account.id());
        }
    }
}