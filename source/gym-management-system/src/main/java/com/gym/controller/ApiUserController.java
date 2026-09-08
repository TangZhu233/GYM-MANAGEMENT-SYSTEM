package com.gym.controller;

import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;
import com.gym.pojo.ClassTable;
import com.gym.pojo.Member;
import com.gym.service.ClassRecordService;
import com.gym.service.ClassTableService;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private ClassTableService classTableService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ClassRecordService classRecordService;

    @GetMapping("/toUserInfo")
    public Map<String, Object> toUserInfo(HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", latestMemberOf(session));
        return resp;
    }

    @GetMapping("/toUpdateInfo")
    public Map<String, Object> toUpdateInfo(HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", latestMemberOf(session));
        return resp;
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<Map<String, Object>> updateUserInformation(HttpSession session, Member member) {
        // 卡内课时/办卡时间以数据库最新为准（避免用会话里的旧值覆盖管理员刚充的课时）
        Member latest = latestMemberOf(session);
        if (latest != null && member != null) {
            member.setMemberAccount(latest.getMemberAccount());
            member.setCardClass(latest.getCardClass());
            member.setCardTime(latest.getCardTime());
            member.setCardNextClass(latest.getCardNextClass());
            memberService.updateMemberByMemberAccount(member);
            session.setAttribute("user", member);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/toUserClass")
    public Map<String, Object> toUserClass(HttpSession session) {
        Member member = latestMemberOf(session);
        String memberAccount = member == null ? null : member.getMemberAccount();
        List<ClassRecordVO> recordList = classRecordService.selectByMemberId(memberAccount);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        resp.put("classRecordList", recordList);
        return resp;
    }

    @Transactional
    @PostMapping("/delUserClass")
    public ResponseEntity<Map<String, Object>> deleteUserClass(Integer recordId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return fail("会话已失效");
        }

        ClassRecord record = classRecordService.selectByRecordId(recordId);
        if (record == null) {
            return fail("报名记录不存在");
        }
        if (!member.getMemberAccount().equals(record.getMemberId())) {
            return fail("不能操作其他会员的报名记录");
        }
        if (record.getStatus() != null && record.getStatus() != 0) {
            return fail("只能退未完成的课程");
        }

        int total = record.getTotalSessions() == null || record.getTotalSessions() < 1
                ? 1 : record.getTotalSessions();
        classRecordService.deleteByRecordId(recordId);
        memberService.updateRemainClassCount(record.getMemberId(), total);
        refreshSessionUser(session, record.getMemberId());
        return success();
    }

    @GetMapping("/toApplyClass")
    public Map<String, Object> toUserApplyClass(HttpSession session) {
        Member member = latestMemberOf(session);
        String memberAccount = member == null ? null : member.getMemberAccount();
        List<ClassTable> classList = classTableService.findAll();
        if (memberAccount != null) {
            Set<Integer> appliedClassIds = classRecordService.selectByMemberId(memberAccount)
                    .stream()
                    .map(ClassRecordVO::getClassId)
                    .collect(Collectors.toSet());
            classList = classList.stream()
                    .filter(classTable -> !appliedClassIds.contains(classTable.getClassId()))
                    .collect(Collectors.toList());
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("member", member);
        resp.put("classList", classList);
        return resp;
    }

    @Transactional
    @PostMapping("/applyClass")
    public ResponseEntity<Map<String, Object>> userApplyClass(Integer classId, Integer coachId, HttpSession session) {
        ClassTable classTable = classTableService.selectByClassId(classId);
        Member member = (Member) session.getAttribute("user");

        if (classTable == null || member == null) {
            return fail("会话已失效或课程不存在");
        }

        // 报名须从该课程任教教练中选择一位（无教练课程无法报名）
        if (coachId == null) {
            return fail("请选择教练后再报名");
        }
        if (!classTableService.isCoachOfClass(classId, coachId)) {
            return fail("所选教练不是该课程任教教练");
        }

        String memberId = member.getMemberAccount();
        Member latestMember = findMember(memberId);
        if (latestMember == null) {
            return fail("会员不存在");
        }

        ClassRecord exist = classRecordService.selectByClassIdAndMemberId(classId, memberId);
        if (exist != null) {
            return fail("你已报名该课程");
        }

        Integer maxCapacity = classTable.getMaxCapacity();
        if (maxCapacity != null && maxCapacity > 0) {
            int enrolledCount = classRecordService.countActiveByClassId(classId);
            if (enrolledCount >= maxCapacity) {
                return fail("课程名额已满");
            }
        }

        Integer total = classTable.getTotalSessions() == null || classTable.getTotalSessions() < 1
                ? 1 : classTable.getTotalSessions();

        Integer remainClassCount = latestMember.getCardNextClass();
        if (remainClassCount == null || remainClassCount < total) {
            return fail("剩余课时不足：报名该课程需 " + total + " 课时");
        }

        int updatedRows = memberService.updateRemainClassCount(memberId, -total);
        if (updatedRows != 1) {
            return fail("剩余课时不足：报名该课程需 " + total + " 课时");
        }

        ClassRecord record = new ClassRecord();
        record.setMemberId(memberId);
        record.setClassId(classId);
        record.setCoachId(coachId);
        record.setTotalSessions(total);
        record.setCompletedSessions(0);
        classRecordService.insertRecord(record);
        refreshSessionUser(session, memberId);
        return success();
    }

    @PostMapping("/rateClass")
    public ResponseEntity<Map<String, Object>> rateClass(Integer recordId, Double rating, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return fail("会话已失效，请重新登录");
        }

        ClassRecord record = classRecordService.selectByRecordId(recordId);
        if (record == null) {
            return fail("报名记录不存在");
        }
        if (!member.getMemberAccount().equals(record.getMemberId())) {
            return fail("不能为其他会员的记录评分");
        }
        if (record.getStatus() != null && record.getStatus() == 2) {
            return fail("已取消报名的记录不能评分");
        }
        if (rating == null || rating < 0 || rating > 10) {
            return fail("评分需在 0 到 10 之间");
        }

        classRecordService.updateRating(recordId, rating);
        return success();
    }

    @PostMapping("/checkInClass")
    public ResponseEntity<Map<String, Object>> checkInClass(Integer recordId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return fail("会话已失效，请重新登录");
        }

        ClassRecord record = classRecordService.selectByRecordId(recordId);
        if (record == null) {
            return fail("报名记录不存在");
        }
        if (!member.getMemberAccount().equals(record.getMemberId())) {
            return fail("不能操作其他会员的记录");
        }
        if (record.getStatus() == null || record.getStatus() != 0) {
            return fail("该课程已完成或已取消，无需打卡");
        }

        int total = record.getTotalSessions() == null || record.getTotalSessions() < 1
                ? 1 : record.getTotalSessions();
        int completed = record.getCompletedSessions() == null ? 0 : record.getCompletedSessions();
        if (completed >= total) {
            return fail("该课程课时已上完");
        }

        int newCompleted = completed + 1;
        int newStatus = newCompleted >= total ? 1 : 0;
        if (newStatus == 1) newCompleted = total;
        classRecordService.updateProgress(recordId, newCompleted, newStatus);
        return success();
    }

    private Member findMember(String memberAccount) {
        List<Member> members = memberService.selectByMemberAccount(memberAccount);
        return members == null || members.isEmpty() ? null : members.get(0);
    }

    private void refreshSessionUser(HttpSession session, String memberAccount) {
        Member latestMember = findMember(memberAccount);
        if (latestMember != null) {
            session.setAttribute("user", latestMember);
        }
    }

    /** 读取会话会员时按会员号查库返回最新（管理员后台改过课时/资料也能同步看到），并刷新会话缓存。 */
    private Member latestMemberOf(HttpSession session) {
        Member cached = (Member) session.getAttribute("user");
        if (cached == null || cached.getMemberAccount() == null) {
            return cached;
        }
        Member latest = findMember(cached.getMemberAccount());
        if (latest != null) {
            session.setAttribute("user", latest);
            return latest;
        }
        return cached;
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
