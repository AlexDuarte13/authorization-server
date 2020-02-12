package com.ebix.easi.authorizationserver.model.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ebix.easi.authorizationserver.model.service.AuthorizationServerService;

@Component
public class BaseProjectInterceptor implements HandlerInterceptor{
	
	@Autowired
	private AuthorizationServerService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
    	
    	if(StringUtils.isBlank(request.getHeader("token")) || !authService.tokenIsValidAdmin(request.getHeader("token"))) {
    		response.setStatus(HttpStatus.UNAUTHORIZED.value());
    		return false;
    	}
    	
    	return true;
    	
    }

}
