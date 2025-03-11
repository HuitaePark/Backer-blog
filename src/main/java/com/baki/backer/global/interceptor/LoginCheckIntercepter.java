package com.baki.backer.global.interceptor;

import com.baki.backer.global.error.ErrorResponse;
import com.baki.backer.global.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;

@Slf4j
public class LoginCheckIntercepter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("로그인 체크 = {}", requestURI);

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            log.info("세션이 없거나 유저 정보가 없습니다.");

            // JSON 에러 응답 설정
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized

            // 에러 메시지 객체 생성 (여기서 ErrorResponse와 ResponseUtil은 기존에 사용하던 클래스라고 가정)
            ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");

            // Jackson ObjectMapper를 이용해 JSON 문자열로 변환
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(ResponseUtil.error(errorResponse));

            // 응답에 JSON 데이터 작성
            PrintWriter writer = response.getWriter();
            writer.write(jsonResponse);
            writer.flush();

            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
