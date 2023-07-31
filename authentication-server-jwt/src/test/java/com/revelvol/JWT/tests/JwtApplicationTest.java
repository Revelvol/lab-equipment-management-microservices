package com.revelvol.JWT.tests;

import com.revelvol.JWT.controller.AuthenticationController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtApplicationTest {

	@Autowired
	private AuthenticationController authenticationController;


	@Test
	void contextLoads() throws Exception {
		Assertions.assertNotNull(authenticationController);
	}

}
