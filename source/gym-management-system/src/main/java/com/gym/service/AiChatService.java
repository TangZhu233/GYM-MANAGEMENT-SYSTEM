package com.gym.service;

public interface AiChatService {

    /** 调用配置的 OpenAI 兼容接口（默认 SiliconFlow 免费模型），返回助手回答文本。 */
    String chat(String systemPrompt, String question);
}
