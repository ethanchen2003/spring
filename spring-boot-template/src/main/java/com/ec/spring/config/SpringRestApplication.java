package com.ec.spring.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.ec.spring.event.ServerContextInitializer;
import com.ec.spring.utility.SystemTimeUtility;

@SpringBootApplication
public class SpringRestApplication extends SpringBootServletInitializer {
	private static final Logger logger = LoggerFactory.getLogger(SpringRestApplication.class);
	
	private static final String DateTimeFormat = "yyyy-MM-dd HH:mm";
	
	private static long serverStartTime = 0;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringRestApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication sa = new SpringApplication(SpringRestApplication.class);
		sa.addInitializers(new ServerContextInitializer());
		commandLinesParse(args);
		sa.run(args);
	}
	
	private static void commandLinesParse(String[] args) {
		try {
			Options options = new Options();
	
			Option stInput = new Option("st", "starttime", true, "sever start time, foramt " + DateTimeFormat);
			stInput.setRequired(false);
			options.addOption(stInput);
	
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			String startTime = cmd.getOptionValue("starttime");
			setServerStartTime(startTime);
		} catch (Exception e) {
			logger.error("error to parse commandline args: " + args, e);
		}
	}	
	
	private static void setServerStartTime(String startTime) throws ParseException {
		if(startTime == null || startTime.isEmpty()) {
			return;
		}
		SimpleDateFormat ft = new SimpleDateFormat(DateTimeFormat);
		Date dt = ft.parse(startTime);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		SystemTimeUtility.setYear(calendar.get(Calendar.YEAR));
		SystemTimeUtility.setMonth(calendar.get(Calendar.MONTH) + 1);
		SystemTimeUtility.setDate(calendar.get(Calendar.DAY_OF_MONTH));
		SystemTimeUtility.setOffset(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}
	
	@Bean("startTime")
	public long startTime() {
		return serverStartTime;
	}	
}
