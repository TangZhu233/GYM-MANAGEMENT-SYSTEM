package com.gym.controller;

import com.gym.pojo.Admin;
import com.gym.pojo.Member;
import com.gym.service.AdminService;
import com.gym.service.EmployeeService;
import com.gym.service.EquipmentService;
import com.gym.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiLoginController {

    private static final String SESSION_ADMIN = "admin";
    private static final String SESSION_USER = "user";

    private final MemberService memberService;
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;

    public ApiLoginController(
            MemberService memberService,
            AdminService adminService,
            EmployeeService employeeService,
            EquipmentService equipmentService) {
        this.memberService = memberService;
        this.adminService = adminService;
        this.employeeService = employeeService;
        this.equipmentService = equipmentService;
    }

    @PostMapping("/adminLogin")
    public ResponseEntity<Map<String, Object>> adminLogin(Admin admin, HttpSession session) {
        Admin loggedIn = adminService.adminLogin(admin);
        if (loggedIn == null) {
            return unauthorized("账号或密码有误");
        }
        putAdminMainDataInSession(session, loggedIn);
        return ResponseEntity.ok(singleSuccess());
    }

    @PostMapping("/userLogin")
    public ResponseEntity<Map<String, Object>> userLogin(Member member, HttpSession session) {
        Member loggedIn = memberService.userLogin(member);
        if (loggedIn == null) {
            return unauthorized("账号或密码有误");
        }
        session.setAttribute(SESSION_USER, loggedIn);
        return ResponseEntity.ok(singleSuccess());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(singleSuccess());
    }

    @GetMapping("/toAdminMain")
    public ResponseEntity<Map<String, Object>> toAdminMain(HttpSession session) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("memberTotal", session.getAttribute("memberTotal"));
        body.put("employeeTotal", session.getAttribute("employeeTotal"));
        body.put("humanTotal", session.getAttribute("humanTotal"));
        body.put("equipmentTotal", session.getAttribute("equipmentTotal"));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/toUserMain")
    public ResponseEntity<Map<String, Object>> toUserMain(HttpSession session) {
        refreshSessionUser(session);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("member", session.getAttribute(SESSION_USER));
        return ResponseEntity.ok(body);
    }

    /** 会话里缓存的会员可能已被管理员在后台修改（如充值课时），读取前按会员号查库刷新，保证用户端看到最新数据。 */
    private void refreshSessionUser(HttpSession session) {
        Member cached = (Member) session.getAttribute(SESSION_USER);
        if (cached == null || cached.getMemberAccount() == null) {
            return;
        }
        List<Member> members = memberService.selectByMemberAccount(cached.getMemberAccount());
        if (members != null && !members.isEmpty()) {
            session.setAttribute(SESSION_USER, members.get(0));
        }
    }

    /** 管理员登录后写入 session：身份 + 主页统计（与原先 adminMain 依赖的 key 一致）。 */
    private void putAdminMainDataInSession(HttpSession session, Admin admin) {
        session.setAttribute(SESSION_ADMIN, admin);
        Integer memberTotal = memberService.selectTotalCount();
        Integer employeeTotal = employeeService.selectTotalCount();
        Integer humanTotal = memberTotal + employeeTotal;
        Integer equipmentTotal = equipmentService.selectTotalCount();
        session.setAttribute("memberTotal", memberTotal);
        session.setAttribute("employeeTotal", employeeTotal);
        session.setAttribute("humanTotal", humanTotal);
        session.setAttribute("equipmentTotal", equipmentTotal);
    }

    private static Map<String, Object> singleSuccess() {
        Map<String, Object> m = new HashMap<>(2);
        m.put("success", true);
        return m;
    }

    private static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        Map<String, Object> m = new HashMap<>(4);
        m.put("success", false);
        m.put("message", message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(m);
    }
}