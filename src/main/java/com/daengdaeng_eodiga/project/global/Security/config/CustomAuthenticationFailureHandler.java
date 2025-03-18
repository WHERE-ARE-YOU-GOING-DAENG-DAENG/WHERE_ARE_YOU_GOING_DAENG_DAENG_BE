package com.daengdaeng_eodiga.project.global.Security.config;

import com.daengdaeng_eodiga.project.oauth.OauthProvider;
import com.daengdaeng_eodiga.project.oauth.controller.OuathController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OuathController ouathController;
    @Value("${frontend.url}")
    private String frontUrl;
    /**
     * 인증 실패 시 호출되는 메소드.
     * @ author 김승환
     * - 인증 실패 메시지를 기반으로 이메일과 제공자를 추출하고, 적절한 처리(회원가입 페이지 리디렉션, 삭제된 사용자 페이지 리디렉션, 또는 오류 페이지 리디렉션)를 수행합니다.
     * - 오류 메시지가 "REDIRECT_TO_SIGNUP"인 경우, 사용자에게 회원가입 페이지를 보여주고, "DELETED_USER"인 경우 삭제된 사용자 페이지로 리디렉션합니다.
     * - 그 외의 경우에는 기본적으로 로그인 페이지로 리디렉션됩니다.
     *
     * @param request HttpServletRequest 객체
     * @param response HttpServletResponse 객체
     * @param exception 인증 실패 시 발생한 예외 객체
     * @throws IOException 입출력 오류가 발생한 경우 예외를 던짐
     * @throws ServletException 서블릿 관련 예외가 발생한 경우 예외를 던짐
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = exception.getMessage();
        String email = null;

        OauthProvider provider = null;
        if (errorMessage != null) {
            if (errorMessage.startsWith("REDIRECT_TO_SIGNUP:")) {
                String[] parts = errorMessage.substring("REDIRECT_TO_SIGNUP:".length()).trim().split(", ");
                for (String part : parts) {
                    if (part.startsWith("email=")) {
                        email = part.substring("email=".length());
                    } else if (part.startsWith("provider=")) {
                        String providerValue = part.substring("provider=".length());
                        try{
                            provider = OauthProvider.valueOf(providerValue.toLowerCase());
                        }catch (Exception e){
                            provider = OauthProvider.google;
                        }

                    }
                }
            }
        }
        if (email == null) {
            email = "unknown@example.com";
        }
        if (errorMessage != null && errorMessage.startsWith("REDIRECT_TO_SIGNUP:")) {
            ouathController.showSignUpForm(email, provider.toString(), response);
        } else if (errorMessage != null && errorMessage.startsWith("DELETED_USER:")) {
            log.info("redirect to DELETE_USER page");
            ouathController.deletedUserRedirect(response);
        } else {
            response.sendRedirect("/login?error=unknown");
        }
    }

}