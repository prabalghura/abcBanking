package com.turvo.abcbanking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.turvo.abcbanking.repository.BaseRepositoryImpl;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.turvo.abcbanking.repository",repositoryBaseClass = BaseRepositoryImpl.class )
public class JPAConfig
{
}