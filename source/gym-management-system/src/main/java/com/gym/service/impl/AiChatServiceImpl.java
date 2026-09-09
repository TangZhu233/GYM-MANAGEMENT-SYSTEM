package com.gym.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.service.AiChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Value("${ai.base-url:https://api.siliconflow.cn/v1}")
    private String baseUrl;

    @Value("${ai.model:Qwen/Qwen2.5-7B-Instruct}")
    private String model;

    @Value("${ai.api-key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String chat(String systemPrompt, String question) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("AI 助手未配置密钥");
        }
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(message("system", systemPrompt));
            messages.add(message("user", question));

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", model);
            payload.put("messages", messages);
            payload.put("stream", false);
            payload.put("temperature", 0.7);

            String body = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new RuntimeException("AI 服务返回异常(" + response.statusCode() + ")：" + brief(response.body()));
            }
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText("").trim();
            if (content.isEmpty()) {
                throw new RuntimeException("AI 没有返回有效内容，请换个问法");
            }
            return content;
        } catch (java.io.IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI 请求失败：" + e.getMessage());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("AI 解析失败：" + e.getMessage());
        }
    }

    private Map<String, Object> message(String role, String content) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("role", role);
        m.put("content", content);
        return m;
    }

    private String brief(String raw) {
        if (raw == null || raw.isBlank()) {
            return "空响应";
        }
        return raw.length() > 200 ? raw.substring(0, 200) + "…" : raw;
    }
}
