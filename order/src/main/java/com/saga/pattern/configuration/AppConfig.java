package com.saga.pattern.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public ConversionService conversionService(List<Converter> converters) {
        GenericConversionService conversionService = new GenericConversionService();
        converters.forEach(conversionService::addConverter);
        return conversionService;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
