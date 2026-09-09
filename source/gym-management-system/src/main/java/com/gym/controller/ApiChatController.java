package com.gym.controller;

import com.gym.pojo.Member;
import com.gym.service.AiChatService;
import com.gym.service.HealthService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 会员端「AI 健康助手」：转发到配置的 OpenAI 兼容接口（默认硅基流动免费模型）。 */
@RestController
@RequestMapping("/api/user")
public class ApiChatController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private HealthService healthService;
    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(String question, HttpSession session) {
        Member member = latestMemberOf(session);
        if (member == null) {
            return fail("会话已失效，请重新登录");
        }
        if (question == null || question.isBlank()) {
            return fail("请输入问题");
        }
        String answer;
        try {
            answer = aiChatService.chat(buildSystemPrompt(member), question.trim());
        } catch (Exception e) {
            return fail(e.getMessage());
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("answer", answer);
        return ResponseEntity.ok(resp);
    }

    /** 把会员当前身高体重 / BMI 拼进人设，让回答更贴合本人。 */
    private String buildSystemPrompt(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是健身房系统的「智能健康助手」，帮会员解答运动训练、饮食搭配、减脂增肌、恢复休息等日常健康问题。");
        sb.append("语气亲切、回答尽量精简实用，一般 3~6 行内；涉及疾病诊断或用药请提醒以线下医生意见为准。");

        String name = member.getMemberName();
        Integer height = member.getMemberHeight();
        Integer weight = member.getMemberWeight();
        if (name != null && !name.isBlank()) {
            sb.append(" 当前咨询的会员：").append(name).append("。");
        }
        if (height != null && height > 0 && weight != null && weight > 0) {
            sb.append(" 身高 ").append(height).append("cm、体重 ").append(weight).append("kg");
            Object bmiLabel = bmiLabelOf(member);
            if (bmiLabel != null) {
                sb.append("，BMI 评级为“").append(bmiLabel).append("”");
            }
            sb.append("；可基于这些数据给个性化建议。");
        } else {
            sb.append(" 该会员暂未填写身高体重，请先给出通用建议并建议其补充身体数据。");
        }
        return sb.toString();
    }

    private Object bmiLabelOf(Member member) {
        try {
            Map<String, Object> data = healthService.overview(member);
            Map<?, ?> bmi = (Map<?, ?>) data.get("bmi");
            return bmi == null ? null : bmi.get("label");
        } catch (Exception e) {
            return null;
        }
    }

    private Member latestMemberOf(HttpSession session) {
        Member cached = (Member) session.getAttribute("user");
        if (cached == null || cached.getMemberAccount() == null) {
            return cached;
        }
        List<Member> list = memberService.selectByMemberAccount(cached.getMemberAccount());
        if (list == null || list.isEmpty()) {
            return cached;
        }
        Member latest = list.get(0);
        session.setAttribute("user", latest);
        return latest;
    }

    private static ResponseEntity<Map<String, Object>> fail(String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", message);
        return ResponseEntity.ok(resp);
    }
}
