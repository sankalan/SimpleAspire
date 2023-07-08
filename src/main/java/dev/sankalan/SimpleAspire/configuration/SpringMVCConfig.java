package dev.sankalan.SimpleAspire.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import dev.sankalan.SimpleAspire.interceptors.AuthInterceptor;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {
	@Autowired
	AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor);
	}

}
