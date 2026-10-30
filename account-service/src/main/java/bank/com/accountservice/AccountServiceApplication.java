package bank.com.accountservice;

import bank.com.accountservice.domain.Account;
import bank.com.accountservice.domain.AccountStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import bank.com.accountservice.repository.AccountRepository;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner init(AccountRepository repo) {
        return args -> {
            repo.save(Account.builder().customerReference("CUST-001")
                    .status(AccountStatus.ACTIVE).currency("EUR").balance(5000).build());
            repo.save(Account.builder().customerReference("CUST-002")
                    .status(AccountStatus.ACTIVE).currency("EUR").balance(1200).build());
            repo.save(Account.builder().customerReference("CUST-003")
                    .status(AccountStatus.BLOCKED).currency("USD").balance(300).build());
        };
    }

}
