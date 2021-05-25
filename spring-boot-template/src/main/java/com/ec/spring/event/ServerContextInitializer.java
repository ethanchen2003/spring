package com.ec.spring.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.ec.spring.utility.SystemTimeUtility;

public class ServerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger logger = LoggerFactory.getLogger(ServerContextInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		logger.info("initialize rest application");
		// anything before context start.
		SystemTimeUtility.setTimezone();
	}

}
