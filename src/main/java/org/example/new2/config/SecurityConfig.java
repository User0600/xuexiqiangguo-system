package org.example.new2.config;

import org.example.new2.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
// ⚠️⚠️⚠️ [极端测试修改 1]：将 prePostEnabled 改为 false (或者直接注释掉)
// 这样 Controller 上的 @PreAuthorize("hasAuthority('ADMIN')") 将全部失效！
// 等测试通过后，记得改回 true
@EnableMethodSecurity(prePostEnabled = false) // ✅ 启用方法级权限注解
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                // ⚠️⚠️⚠️ 修改这里：为了测试，暂时允许所有来源
                // 测试完成后，记得改回 List.of("http://localhost:5173", ...) 以保证安全
                "*"
//                "http://localhost:5173",
//                "http://127.0.0.1:5173",
//                "http://localhost:3000",
//                "http://127.0.0.1:3000"
        ));

        // ✅ 修复 2：简化配置
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 允许所有请求头
        config.setExposedHeaders(List.of("Authorization","token"));//我添加了“token” 暴露 header
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 建议改成 /** 匹配范围更大
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                // 1. 静态资源与跨域预检
                                  // ✅ 公开接口（无需登录),处理 OPTIONS 预检请求 (浏览器跨域必须)
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 建议改成 /**
                                .requestMatchers("/images/**").permitAll()



                         // 2. 公开业务接口
                        // 2. 登录注册接口 (公开),,,完全公开的接口 (登录注册、题库浏览、文件访问)
                        .requestMatchers("/api/admin/login", "/api/admin/register").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                        // 🔥🔥🔥 3. 新增：放行题库所有接口 (用于测试) 🔥🔥🔥
                        // 这样即使不传 Token，或者 Token 格式不对，也可以访问提交和查询
                        .requestMatchers("/api/question/**","/api/task/**").permitAll()
                        // 在 filterChain 方法的 authorizeHttpRequests 中添加,为了方便测试下载
                        .requestMatchers("/api/stats/**").permitAll()
                        // 1. 放行文件上传接口
                        .requestMatchers("/api/file/**").permitAll()
                        // 放行 Swagger/Knife4j 文档资源
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()

                        // 3. 🔥 个人中心接口 (必须在 ADMIN 规则之前)
                        // 只要登录(authenticated)即可，不区分 USER/ADMIN
                                // 必须放在管理员规则之前！否则会被 /api/user/* 拦截
                                .requestMatchers(
                                        "/api/user/info",      // 获取个人信息
                                        "/api/user/profile",   // 修改资料
                                        "/api/user/password"   // 修改密码
                                ).authenticated()

                    // 4. 管理员接口 (精准控制)
                                // 注意：这里只限制特定的管理接口，或者 DELETE 操作
                        .requestMatchers("/api/user/list").hasAuthority("ADMIN")
                        .requestMatchers("/api/user/edit").hasAuthority("ADMIN")
                                // 🔥 修改点：只拦截 DELETE 方法，而不是所有 /api/user/**
                                // 这样避免误伤其他未定义的 GET/PUT 接口
                                .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasAuthority("ADMIN")


                        // ✅ 其他接口需要认证（普通用户可访问自己的信息）
                        // 5. ⚠️⚠️⚠️ 兜底规则必须永远在最后一行 ⚠️⚠️⚠️
                        .anyRequest().authenticated()


                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}