package com.expensesplit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartExpenseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Expense Split & Settlement System API")
                        .description("Industry-standard REST APIs for expense splitting and smart settlement optimization using Greedy Algorithms and PriorityQueues.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("dev@expensesplit.com"))
                        .license(new License().name("Apache 2.0")));
    }
}

