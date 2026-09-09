package com.gym.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public SessionAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    private boolean isPublicApi(String uri){
        return  uri ==null
                || uri.startsWith("/api/adminLogin")
                || uri.startsWith("/api/userLogin")
                || uri.startsWith("/api/logout");
    }

    private boolean isUserApi (String uri){
        return uri != null &&(
                uri.equals("/api/toUserMain")
                || uri.startsWith("/api/user/")
                );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }

        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api")){
            return true;
        }

        if (isPublicApi(uri)){
            return true;
        }

        boolean requireUser = isUserApi(uri);
        String requiredSessionKey = requireUser ? "user" : "admin";
        // 营业状态只读 GET：管理员/会员任一已登录会话均可读（打烊仅是提示，不拦截操作）
        boolean anyRoleRead = "/api/business/status".equals(uri) && "GET".equalsIgnoreCase(request.getMethod());

        HttpSession session = request.getSession(false);
        boolean authed;
        if (session == null) {
            authed = false;
        } else if (anyRoleRead) {
            authed = session.getAttribute("admin") != null || session.getAttribute("user") != null;
        } else {
            authed = session.getAttribute(requiredSessionKey) != null;
        }
        if (authed) {
            return true;
        }


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", "未登录");
        response.getWriter().write(objectMapper.writeValueAsString(resp));
        return false;

    }
}
