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

    //删除某教练名下全部任教记录（class_coach）
    int deleteCoachesByCoach(String employeeAccount);

    //把若干课程加入某教练任教名单（class_coach）
    int insertCoachesForCoach(String employeeAccount, List<Integer> classIds);

    //删除某课程的全部任教教练
    int deleteClassCoaches(Integer classId);

    //给某课程写入若干任教教练
    int insertClassCoaches(Integer classId, List<Integer> coachIds);

    //重算一批课程的主教练
    int recomputePrimaryCoaches(List<Integer> classIds);

    //解雇守卫：某员工仍在任教名单/主教练上的课程数
    int countActiveTeaching(String employeeAccount);

    //某教练任教的课程 id 列表
    List<Integer> selectClassIdsByCoach(String employeeAccount);

    //校验某教练是否在某课程任教名单
    boolean isCoachOfClass(Integer classId, Integer coachId);

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
