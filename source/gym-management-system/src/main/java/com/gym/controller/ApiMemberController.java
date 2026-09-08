package com.gym.controller;

import com.gym.pojo.Member;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class ApiMemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/selMember")
    public Map<String, Object> selectMember() {
        List<Member> memberList = memberService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("memberList", memberList);
        return resp;
    }

    @GetMapping("/search")
    public Map<String, Object> searchMember(String keyword, String gender, Integer minRemain, Integer maxRemain,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        List<Member> memberList = memberService.searchMembers(keyword, gender, minRemain, maxRemain, page, pageSize);
        int total = memberService.countSearchMembers(keyword, gender, minRemain, maxRemain);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("memberList", memberList);
        resp.put("total", total);
        return resp;
    }

    @GetMapping("/toAddMember")
    public Map<String, Object> toAddMember() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addMember")
    public ResponseEntity<Map<String, Object>> addMember(Member member) {
        String yearPrefix = "2026";
        String maxNo = memberService.selectMaxNoByYear(yearPrefix);
        int sequence = maxNo == null ? 1 : Integer.parseInt(maxNo.substring(yearPrefix.length())) + 1;
        String account = yearPrefix + String.format("%04d", sequence);

        String password = "123456";

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = simpleDateFormat.format(date);

        Integer nextClass = member.getCardClass();

        member.setMemberAccount(account);
        member.setMemberPassword(password);
        member.setCardTime(nowDay);
        member.setCardNextClass(nextClass);

        memberService.insertMember(member);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delMember")
    public ResponseEntity<Map<String, Object>> deleteMember(String memberAccount) {
        memberService.deleteByMemberAccount(memberAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUpdateMember")
    public Map<String, Object> toUpdateMember(String memberAccount) {
        List<Member> memberList = memberService.selectByMemberAccount(memberAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("memberList", memberList);
        return resp;
    }

    @PostMapping("/updateMember")
    public ResponseEntity<Map<String, Object>> updateMember(Member member) {
        if (member == null || member.getMemberAccount() == null) {
            return ResponseEntity.ok(buildResp(false, "参数不完整"));
        }

        // 一致性校验：0 ≤ 剩余课时 ≤ 购买课时（购买增大时由前端把差额自动补到剩余课时）
        Integer total = member.getCardClass();
        Integer remain = member.getCardNextClass();
        if (total == null || total < 1) {
            return ResponseEntity.ok(buildResp(false, "购买课时需大于等于 1"));
        }
        if (remain == null || remain < 0) {
            return ResponseEntity.ok(buildResp(false, "剩余课时不能为负数"));
        }
        if (remain > total) {
            return ResponseEntity.ok(buildResp(false, "剩余课时不能大于购买课时，请先核对课时数"));
        }

        memberService.updateMemberByMemberAccount(member);
        return ResponseEntity.ok(buildResp(true, null));
    }

    private static Map<String, Object> buildResp(boolean success, String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", success);
        if (message != null) {
            resp.put("message", message);
        }
        return resp;
    }

    @GetMapping("/toSelByCard")
    public Map<String, Object> toSelByCard() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/selByCard")
    public Map<String, Object> selectByCardId(String memberAccount) {
        List<Member> memberList = memberService.selectByMemberAccount(memberAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        if (memberList != null) {
            resp.put("memberList", memberList);
        } else {
            resp.put("noMessage", "会员卡号不存在！");
        }
        return resp;
    }
}
