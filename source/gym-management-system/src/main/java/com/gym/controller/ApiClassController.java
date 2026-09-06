package com.gym.controller;

import com.gym.pojo.ClassRecordVO;
import com.gym.pojo.ClassTable;
import com.gym.pojo.Equipment;
import com.gym.service.ClassRecordService;
import com.gym.service.ClassTableService;
import com.gym.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class")
public class ApiClassController {

    @Autowired
    private ClassTableService classTableService;
    @Autowired
    private ClassRecordService classRecordService;
    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/selClass")
    public Map<String, Object> selectClass() {
        List<ClassTable> classList = classTableService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classList", classList);
        return resp;
    }

    @GetMapping("/selClassRecord")
    public Map<String, Object> selectClassRecord(Integer classId) {
        List<ClassRecordVO> recordList = classRecordService.selectByClassId(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classRecordList", recordList);
        return resp;
    }

    @GetMapping("/record/search")
    public Map<String, Object> searchClassRecord(Integer classId, String className, String memberKeyword, Integer status,
                                                 String ratingStatus, Double lowRating, Double highRating,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int pageSize,
                                                 @RequestParam(defaultValue = "true") boolean includeTotal) {
        List<ClassRecordVO> recordList = classRecordService.searchRecords(
                classId, className, memberKeyword, status, ratingStatus, lowRating, highRating, page, pageSize
        );
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classRecordList", recordList);
        if (includeTotal) {
            int total = classRecordService.countSearchRecords(
                    classId, className, memberKeyword, status, ratingStatus, lowRating, highRating
            );
            resp.put("total", total);
        }
        return resp;
    }

    @GetMapping("/analysis")
    public Map<String, Object> analysis(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("summary", classTableService.selectAnalysisSummary());
        resp.put("courseAnalysisRows", classTableService.selectCourseAnalysisRows(page, pageSize));
        resp.put("total", classTableService.countCourseAnalysisRows());
        return resp;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("summary", classTableService.selectAnalysisSummary());
        resp.put("topByRating", classTableService.selectDashboardTopByRating());
        resp.put("topByEnrollment", classTableService.selectDashboardTopByEnrollment());
        return resp;
    }

    @GetMapping("/ratingView")
    public Map<String, Object> ratingView(Double minRating, String gender,
                                          @RequestParam(defaultValue = "50") int limit) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("viewName", "v_member_course_rating");
        resp.put("ratingRows", classRecordService.selectRatingView(minRating, gender, limit));
        return resp;
    }

    @GetMapping("/toAddClass")
    public Map<String, Object> toAddClass() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addClass")
    public ResponseEntity<Map<String, Object>> addClass(ClassTable classTable) {
        if (classTable.getTotalSessions() == null || classTable.getTotalSessions() < 1) {
            classTable.setTotalSessions(1);
        }
        classTableService.insertClass(classTable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/updateClassSessions")
    public ResponseEntity<Map<String, Object>> updateClassSessions(Integer classId, Integer totalSessions) {
        Map<String, Object> resp = new HashMap<>();
        if (classId == null || totalSessions == null || totalSessions < 1) {
            resp.put("success", false);
            resp.put("message", "总课时需为不小于 1 的整数");
            return ResponseEntity.ok(resp);
        }
        classTableService.updateTotalSessions(classId, totalSessions);
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/classDetail")
    public Map<String, Object> classDetail(Integer classId) {
        ClassTable cls = classTableService.selectByClassId(classId);
        List<Equipment> equipList = equipmentService.selectByClassId(classId);
        int enrolledCount = classRecordService.countByClassId(classId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", cls != null);
        resp.put("course", cls);
        resp.put("equipmentList", equipList);
        resp.put("enrolledCount", enrolledCount);
        return resp;
    }

    @Transactional
    @PostMapping("/delClass")
    public ResponseEntity<Map<String, Object>> deleteClass(Integer classId) {
        classTableService.deleteRecordByClassId(classId);
        classTableService.deleteClassByClassId(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}
