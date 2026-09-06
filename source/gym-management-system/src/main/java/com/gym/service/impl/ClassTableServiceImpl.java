package com.gym.service.impl;

import com.gym.mapper.ClassTableMapper;
import com.gym.pojo.ClassTable;
import com.gym.service.ClassTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service
public class ClassTableServiceImpl implements ClassTableService {

    @Autowired
    private ClassTableMapper classTableMapper;

    @Override
    public List<ClassTable> findAll() {
        return classTableMapper.findAll();
    }

    @Override
    public Boolean deleteClassByClassId(Integer classId) {
        return classTableMapper.deleteClassByClassId(classId);
    }

    @Override
    public Boolean insertClass(ClassTable classTable) {
        return classTableMapper.insertClass(classTable);
    }

    @Override
    public int updateTotalSessions(Integer classId, Integer totalSessions) {
        return classTableMapper.updateTotalSessions(classId, totalSessions);
    }

    @Override
    public ClassTable selectByClassId(Integer classId) {
        return classTableMapper.selectByClassId(classId);
    }

    @Override
    public Boolean deleteRecordByClassId(Integer classId) {
        return classTableMapper.deleteRecordByClassId(classId);
    }

    @Override
    public Map<String, Object> selectAnalysisSummary() {
        return classTableMapper.selectAnalysisSummary();
    }

    @Override
    public List<Map<String, Object>> selectCourseAnalysisRows(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        int offset = (page - 1) * pageSize;
        return classTableMapper.selectCourseAnalysisRows(offset, pageSize);
    }

    @Override
    public int countCourseAnalysisRows() {
        return classTableMapper.countCourseAnalysisRows();
    }

    @Override
    public List<Map<String, Object>> selectDashboardTopByRating() {
        return classTableMapper.selectDashboardTopByRating();
    }

    @Override
    public List<Map<String, Object>> selectDashboardTopByEnrollment() {
        return classTableMapper.selectDashboardTopByEnrollment();
    }
}
