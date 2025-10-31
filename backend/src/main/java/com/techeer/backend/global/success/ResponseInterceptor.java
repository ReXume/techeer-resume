package com.techeer.backend.global.success;

import com.techeer.backend.global.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // ResponseEntity<ApiResponse>인 경우, ResponseEntity의 상태 코드를 사용
        if (body instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) body;
            if (responseEntity.getBody() instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) responseEntity.getBody();
                // ResponseEntity의 상태 코드를 사용
                response.setStatusCode(responseEntity.getStatusCode());
                return apiResponse; // ResponseEntity 래퍼를 제거하고 ApiResponse만 반환
            }
        }

        return body;
    }
}