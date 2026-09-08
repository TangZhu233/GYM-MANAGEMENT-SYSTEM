package com.gym.controller;

import com.gym.pojo.Employee;
import com.gym.service.ClassTableService;
import com.gym.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/employee")
public class ApiEmployeeController {

    private static final String ROLE_COACH = "健身教练";

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ClassTableService classTableService;

    @GetMapping("/selEmployee")
    public Map<String, Object> selectEmployee() {
        List<Employee> employeeList = employeeService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("employeeList", employeeList);
        return resp;
    }

    @GetMapping("/search")
    public Map<String, Object> searchEmployee(String keyword, String gender, String staff,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        List<Employee> employeeList = employeeService.searchEmployees(keyword, gender, staff, page, pageSize);
        int total = employeeService.countSearchEmployees(keyword, gender, staff);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("employeeList", employeeList);
        resp.put("total", total);
        return resp;
    }

    @GetMapping("/toAddEmployee")
    public Map<String, Object> toAddEmployee() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @Transactional
    @PostMapping("/addEmployee")
    public ResponseEntity<Map<String, Object>> addEmployee(Employee employee, String courseIds) {
        String maxNo = employeeService.selectMaxEmployeeNo();
        int sequence = maxNo == null ? 1 : Integer.parseInt(maxNo) + 1;
        String account = String.format("%03d", sequence);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowDay = simpleDateFormat.format(date);

        employee.setEmployeeAccount(account);
        employee.setEntryTime(nowDay);

        employeeService.insertEmployee(employee);

        // 健身教练入职可一次挂课（把勾选的课程加入他的任教名单）
        if (ROLE_COACH.equals(employee.getStaff())) {
            List<Integer> ids = parseCourseIds(courseIds);
            if (!ids.isEmpty()) {
                classTableService.insertCoachesForCoach(account, ids);
                classTableService.recomputePrimaryCoaches(ids);
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/delEmployee")
    public ResponseEntity<Map<String, Object>> deleteEmployee(String employeeAccount) {
        int teachingCount = classTableService.countActiveTeaching(employeeAccount);
        if (teachingCount > 0) {
            return ResponseEntity.ok(buildResp(false, "该员工名下仍有 " + teachingCount + " 门任教科目的课程，请先在员工编辑中清空/改派所教课程后再解雇"));
        }
        employeeService.deleteByEmployeeAccount(employeeAccount);
        return ResponseEntity.ok(buildResp(true, null));
    }

    @GetMapping("/toUpdateEmployee")
    public Map<String, Object> toUpdateEmployee(String employeeAccount) {
        List<Employee> employeeList = employeeService.selectByEmployeeAccount(employeeAccount);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("employeeList", employeeList);
        return resp;
    }

    @Transactional
    @PostMapping("/updateEmployee")
    public ResponseEntity<Map<String, Object>> updateEmployee(Employee employee, String courseIds) {
        employeeService.updateMemberByEmployeeAccount(employee);

        // 任教名单全量覆盖：先取旧名单、删除，再按勾选重插，最后对旧∪新课程重算主教练
        String account = employee.getEmployeeAccount();
        List<Integer> old = classTableService.selectClassIdsByCoach(account);
        classTableService.deleteCoachesByCoach(account);

        Set<Integer> affected = new HashSet<>(old);
        if (ROLE_COACH.equals(employee.getStaff())) {
            List<Integer> ids = parseCourseIds(courseIds);
            if (!ids.isEmpty()) {
                classTableService.insertCoachesForCoach(account, ids);
                affected.addAll(ids);
            }
        }
        classTableService.recomputePrimaryCoaches(new ArrayList<>(affected));

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    /** 表单 courseIds 为逗号分隔的课程 id 串（可为空串）。 */
    private static List<Integer> parseCourseIds(String courseIds) {
        List<Integer> ids = new ArrayList<>();
        if (courseIds == null || courseIds.isBlank()) {
            return ids;
        }
        for (String part : courseIds.split(",")) {
            try {
                ids.add(Integer.valueOf(part.trim()));
            } catch (NumberFormatException ignored) {
                // 忽略非法项
            }
        }
        return ids;
    }

    private static Map<String, Object> buildResp(boolean success, String message) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", success);
        if (message != null) {
            resp.put("message", message);
        }
        return resp;
    }
}
