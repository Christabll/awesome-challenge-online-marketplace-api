package com.awesomity.marketplace.marketplace_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;



@SpringBootTest
@ActiveProfiles("test")
class MarketplaceApiApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("🔧 Running with test profile...");
	}

}
