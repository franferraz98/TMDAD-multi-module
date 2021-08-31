package com.example.multimodule.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.multimodule.service.MessagingRabbitmqApplication;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class DemoApplicationTest {

	@Autowired
	private MessagingRabbitmqApplication myService;

	@Test
	public void contextLoads() {
	}

}
