package com.ebix.easi.authorizationserver.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ebix.easi.authorizationserver.model.api.interceptor.BaseProjectInterceptor;

@Component
public class WebMvcConfig implements WebMvcConfigurer{

	@Autowired
	private BaseProjectInterceptor baseProjectInterceptor;

	@Override
	   public void addInterceptors(InterceptorRegistry registry) {
		      registry.addInterceptor(baseProjectInterceptor).addPathPatterns("/authorization/register","/authorization/register/");
	   }

}
