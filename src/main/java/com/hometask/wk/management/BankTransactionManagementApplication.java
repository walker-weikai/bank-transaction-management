package com.hometask.wk.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BankTransactionManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransactionManagementApplication.class, args);
    }

}
