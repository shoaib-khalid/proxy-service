package com.kalsym.proxyservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import com.kalsym.proxyservice.repository.CustomRepositoryImpl;

import org.springframework.http.converter.HttpMessageConverter;
import java.awt.image.BufferedImage;

import org.springframework.http.converter.BufferedImageHttpMessageConverter;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)

public class ProxyServiceApplication {

	public static String VERSION;

    static {
        System.setProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    }

	public static void main(String[] args) {
		SpringApplication.run(ProxyServiceApplication.class, args);
	}

	@Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

}
