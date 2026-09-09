package com.gym.controller;

import com.gym.pojo.BusinessStatus;
import com.gym.service.BusinessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class ApiBusinessController {

    @Autowired
    private BusinessStatusService businessStatusService;

    /** 营业状态只读：管理员/会员任意已登录会话均可读（打烊仅是提示，不拦截操作）。 */
    @GetMapping("/status")
    public Map<String, Object> status() {
        BusinessStatus bs = businessStatusService.selectStatus();
        int s = (bs == null || bs.getStatus() == null) ? 1 : bs.getStatus();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("status", s);
        resp.put("open", s == 1);
        resp.put("updateTime", bs == null ? null : bs.getUpdateTime());
        return resp;
    }

    /** 修改营业状态：默认需管理员会话。 */
    @PostMapping("/setStatus")
    public ResponseEntity<Map<String, Object>> setStatus(Integer status) {
        Map<String, Object> resp = new HashMap<>();
        if (status == null || (status != 0 && status != 1)) {
            resp.put("success", false);
            resp.put("message", "营业状态需为 0(打烊) 或 1(营业中)");
            return ResponseEntity.ok(resp);
        }
        int rows = businessStatusService.updateStatus(status);
        if (rows != 1) {
            resp.put("success", false);
            resp.put("message", "营业状态更新失败");
            return ResponseEntity.ok(resp);
        }
        resp.put("success", true);
        resp.put("status", status);
        return ResponseEntity.ok(resp);
    }
}
