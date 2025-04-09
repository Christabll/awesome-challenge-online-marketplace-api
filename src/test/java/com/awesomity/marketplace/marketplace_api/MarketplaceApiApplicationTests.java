package com.awesomity.marketplace.marketplace_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MarketplaceApiApplicationTests {

	@MockitoBean
	private JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
		System.out.println("🧪 Running with test profile...");
	}
}
