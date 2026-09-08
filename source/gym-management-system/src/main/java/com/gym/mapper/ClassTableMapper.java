package com.gym.mapper;

import com.gym.pojo.ClassTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassTableMapper {

    //查询所有课程
    List<ClassTable> findAll();

    //根据id删除课程
    Boolean deleteClassByClassId(Integer classId);

    //添加课程
    Boolean insertClass(ClassTable classTable);

    //修改课程总课时(节)
    int updateTotalSessions(@Param("classId") Integer classId, @Param("totalSessions") Integer totalSessions);

    //删除某教练名下全部任教记录（class_coach）
    int deleteCoachesByCoach(@Param("employeeAccount") String employeeAccount);

    //把若干课程加入某教练任教名单（class_coach）
    int insertCoachesForCoach(@Param("employeeAccount") String employeeAccount, @Param("classIds") List<Integer> classIds);

    //删除某课程的全部任教教练（class_coach）
    int deleteClassCoaches(@Param("classId") Integer classId);

    //给某课程写入若干任教教练（class_coach）
    int insertClassCoaches(@Param("classId") Integer classId, @Param("coachIds") List<Integer> coachIds);

    //重算一批课程的主教练（coach_id = 名单中最小者；名单空置 NULL）
    int recomputePrimaryCoaches(@Param("classIds") List<Integer> classIds);

    //解雇守卫：某员工仍在任教名单/主教练上的课程数
    int countActiveTeaching(@Param("employeeAccount") String employeeAccount);

    //某教练任教的课程 id 列表（编辑回显/改派重算用）
    List<Integer> selectClassIdsByCoach(@Param("employeeAccount") String employeeAccount);

    //校验某教练是否在某课程任教名单（报名选教练用）
    int isCoachOfClass(@Param("classId") Integer classId, @Param("coachId") Integer coachId);

    //根据id查询课表
    ClassTable selectByClassId(Integer classId);

    //根据id删除已预约的课程
    Boolean deleteRecordByClassId(Integer classId);

    Map<String, Object> selectAnalysisSummary();

    List<Map<String, Object>> selectCourseAnalysisRows(@Param("offset") int offset,
                                                       @Param("pageSize") int pageSize);

    int countCourseAnalysisRows();

    List<Map<String, Object>> selectDashboardTopByRating();

    List<Map<String, Object>> selectDashboardTopByEnrollment();

}
