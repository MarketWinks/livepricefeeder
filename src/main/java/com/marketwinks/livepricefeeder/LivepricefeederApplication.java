package com.marketwinks.livepricefeeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.marketwinks.livepricefeeder.repository.UK_LSE_5Mins_LiveMarketMacdRepository;
import com.marketwinks.livepricefeeder.repository.UK_LSE_5Mins_LiveMarketPriceRepository;

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan(basePackages = { "com.*" })
@EnableMongoRepositories(basePackages = "com.marketwinks.livepricefeeder.repository")
@EnableCaching
public class LivepricefeederApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivepricefeederApplication.class, args);
	}

	@Autowired
	UK_LSE_5Mins_LiveMarketPriceRepository UK_LSE_5Mins_LiveMarketPriceRepository;

	@Autowired
	UK_LSE_5Mins_LiveMarketMacdRepository UK_LSE_5Mins_LiveMarketMacdRepository;

}
