package com.awesomity.marketplace.marketplace_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
class MarketplaceApiApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("🔧 Running with test profile...");
	}

}
