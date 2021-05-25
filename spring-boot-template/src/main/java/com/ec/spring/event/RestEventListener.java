package com.ec.spring.event;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.LoggerContext;

@Component
public class RestEventListener {
	private static final Logger logger = LoggerFactory.getLogger(RestEventListener.class);

	public RestEventListener() {
		logger.info(" RestEventListener ");
	}
	
	/**
	 * The event will be triggered after spring context is created
	 * Application initialization can be put here
	 * @param event
	 */
	@EventListener
    public void handleRefreshedEvent(ContextRefreshedEvent event) {
		logger.info(" ContextRefreshedEvent ");
		TimeZone timeZone = TimeZone.getDefault();
		logger.info("System timezone:  DisplayName - " + timeZone.getDisplayName() + ", ID - " + timeZone.getID());
    }
	/**
	 * The event will be triggered before spring context is close
	 * Application resource close process can be put here
	 * @param event
	 */
	@EventListener
	public void handleCloseEvent(ContextClosedEvent event) {
		logger.info(" ContextClosedEvent ");
		//Stop and release Resource of Log back
		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			loggerContext.stop();
		} catch(Throwable e) {
			logger.error("Error to initDemoData.", e);
		}		
	}
}
