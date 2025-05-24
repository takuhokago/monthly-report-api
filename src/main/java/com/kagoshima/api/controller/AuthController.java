package com.kagoshima.api.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagoshima.api.dto.LoginRequest;
import com.kagoshima.api.dto.LoginResponse;
import com.kagoshima.entity.Employee;
import com.kagoshima.repository.EmployeeRepository;
import com.kagoshima.service.UserDetail;
import com.kagoshima.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(EmployeeRepository employeeRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Employee employee = employeeRepository.findById(request.getCode())
                .orElseThrow(() -> new BadCredentialsException("社員番号が存在しません"));

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadCredentialsException("パスワードが一致しません");
        }

        // JWTの生成
        String token = jwtUtil.generateToken(employee);

        // JWTをHttpOnly Cookieに格納
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true) // 本番は true
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();


        // Cookieをレスポンスに追加
        response.addHeader("Set-Cookie", cookie.toString());

        // レスポンスボディにtokenは含めず、その他情報のみ返す
        LoginResponse loginResponse = new LoginResponse(
                null, // tokenは返さない
                employee.getCode(),
                employee.getFullName(),
                employee.getRole().toString(),
                employee.getEmail(),
                employee.getDepartment().getName(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(loginResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Cookie削除（有効期限0で上書き）
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // 本番では true
                .path("/")
                .sameSite("Strict")
                .maxAge(0) // ← これで削除
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok("ログアウトしました");
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetail userDetail, HttpServletRequest request) {
    	// ↓debug用
    	String cookieHeader = request.getHeader("Cookie");
        System.out.println("【Cookieヘッダー】: " + cookieHeader);
    	// ↑debug用
        if (userDetail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未ログインです");
        }

        Employee employee = userDetail.getEmployee();

        return ResponseEntity.ok(Map.of(
            "code", employee.getCode(),
            "name", employee.getFullName(),
            "role", employee.getRole().toString(),
            "email", employee.getEmail()
        ));
    }



}
