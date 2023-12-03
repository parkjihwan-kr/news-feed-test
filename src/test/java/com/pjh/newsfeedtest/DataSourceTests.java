package com.pjh.newsfeedtest;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@TestConfiguration
public class DataSourceTests {
    @Primary
    @Bean
    public DataSource dataSource(){
        HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;MODE=MySQL")
                .username("sa")
                .password("")
                .build();

        return hikariDataSource;
    }
}
