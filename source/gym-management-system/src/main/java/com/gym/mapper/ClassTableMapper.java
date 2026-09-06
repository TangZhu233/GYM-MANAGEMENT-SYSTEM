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
