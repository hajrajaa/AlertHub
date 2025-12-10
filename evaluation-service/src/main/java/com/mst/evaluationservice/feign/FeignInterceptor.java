package com.mst.evaluationservice.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String rolesHeader = request.getHeader("X-User-Roles");
            String userIdHeader = request.getHeader("X-User-Id");
            String userEmail = request.getHeader("X-User-Email");

            if (rolesHeader != null) {
                requestTemplate.header("X-User-Roles", rolesHeader);
            }
            if (userIdHeader != null) {
                requestTemplate.header("X-User-Id", userIdHeader);
            }
            if (userIdHeader != null) {
                requestTemplate.header("X-User-Email", userEmail);
            }

        }
    }
}
