package com.gym.controller;

import com.gym.pojo.Member;
import com.gym.service.HealthService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 会员端「健康评估」：每日运动打卡 / BMI 评分 / 饮食推荐（纯规则，不含 AI）。 */
@RestController
@RequestMapping("/api/user/health")
public class ApiHealthController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private HealthService healthService;

    @GetMapping("/overview")
    public Map<String, Object> overview(HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        Member member = latestMemberOf(session);
        if (member == null) {
            resp.put("success", false);
            resp.put("message", "会话已失效");
            return resp;
        }
        Map<String, Object> data = healthService.overview(member);
        resp.put("success", true);
        resp.put("member", data.get("member"));
        resp.put("checkin", data.get("checkin"));
        resp.put("bmi", data.get("bmi"));
        resp.put("diet", data.get("diet"));
        return resp;
    }

    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkin(HttpSession session) {
        Member member = latestMemberOf(session);
        if (member == null) {
            return fail("会话已失效");
        }
        boolean ok = healthService.checkIn(member.getMemberAccount());
        if (!ok) {
            return fail("今天已打卡");
        }
        return success();
    }

    /** 读取会话会员时按会员号查库返回最新（同 ApiUserController 的 helper）。 */
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

    private static ResponseEntity<Map<String, Object>> success() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    private static ResponseEntity<Map<String, Object>> fail(String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", false);
        resp.put("message", message);
        return ResponseEntity.ok(resp);
    }
}
