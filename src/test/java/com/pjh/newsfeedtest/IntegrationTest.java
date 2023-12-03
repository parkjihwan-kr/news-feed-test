package com.pjh.newsfeedtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@ContextConfiguration(classes = {DataSourceTests.class})
@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

}
