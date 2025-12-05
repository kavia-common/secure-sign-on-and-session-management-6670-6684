package com.example.authbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AuthBackendApplication.class, properties = {
        "oauth.jwt.secret=01234567890123456789012345678901",
        "oauth.jwt.issuer=test",
        "oauth.dev.loginEnabled=true"
})
class ContextLoadsTest {

    @Test
    void contextLoads() {
        // Application context should start
    }
}
