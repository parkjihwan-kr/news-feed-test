package com.sparta.newsfeed;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Log4j2
public class DataSourceTests {
    @Autowired
    DataSource dataSource;

    @DisplayName("간단한 커넥션 테스트")
    @Test
    void testConnection() throws SQLException {
        @Cleanup Connection conn = dataSource.getConnection();
        log.info(conn);
        assertThat(conn).isNotNull();
    }

}
