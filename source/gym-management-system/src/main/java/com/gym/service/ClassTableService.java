package com.gym.service;

import com.gym.pojo.ClassTable;

import java.util.List;
import java.util.Map;

public interface ClassTableService {

    //查询所有课程
    List<ClassTable> findAll();

    //根据id删除课程
    Boolean deleteClassByClassId(Integer classId);

    //添加课程
    Boolean insertClass(ClassTable classTable);

    //修改课程总课时(节)
    int updateTotalSessions(Integer classId, Integer totalSessions);

    //根据id查询课表
    ClassTable selectByClassId(Integer classId);

    //根据id删除课程的所有报名记录
    Boolean deleteRecordByClassId(Integer classId);

    Map<String, Object> selectAnalysisSummary();

    List<Map<String, Object>> selectCourseAnalysisRows(int page, int pageSize);

    int countCourseAnalysisRows();

    List<Map<String, Object>> selectDashboardTopByRating();

    List<Map<String, Object>> selectDashboardTopByEnrollment();

}
