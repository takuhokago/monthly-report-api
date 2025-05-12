package com.kagoshima.security;

import com.kagoshima.service.UserDetailService;
import com.kagoshima.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String username = null;

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("[JwtFilter] CookieからJWT取得: " + token); // 🔍
                    break;
                }
            }
        }

        if (token != null) {
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("[JwtFilter] トークンから抽出したユーザー名: " + username); // 🔍
            } catch (Exception e) {
                System.out.println("[JwtFilter] JWT抽出エラー: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[JwtFilter] 認証成功: SecurityContextに設定"); // 🔍
            } else {
                System.out.println("[JwtFilter] JWTが無効です"); // 🔍
            }
        } else if (username != null) {
            System.out.println("[JwtFilter] 認証済み: SecurityContextに既に認証情報あり"); // 🔍
        } else {
            System.out.println("[JwtFilter] ユーザー名がnullのため認証スキップ"); // 🔍
        }

        filterChain.doFilter(request, response);
    }


}
