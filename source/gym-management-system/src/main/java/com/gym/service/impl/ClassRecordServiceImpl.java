package com.gym.service.impl;

import com.gym.mapper.ClassRecordMapper;
import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;
import com.gym.service.ClassRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClassRecordServiceImpl implements ClassRecordService {

    @Autowired
    private ClassRecordMapper classRecordMapper;

    @Override public List<ClassRecordVO> findAll() { return classRecordMapper.findAll(); }
    @Override public Boolean insertRecord(ClassRecord record) { return classRecordMapper.insertRecord(record); }
    @Override public List<ClassRecordVO> selectByMemberId(String memberId) { return classRecordMapper.selectByMemberId(memberId); }
    @Override public Boolean deleteByRecordId(Integer recordId) { return classRecordMapper.deleteByRecordId(recordId); }
    @Override public ClassRecord selectByRecordId(Integer recordId) { return classRecordMapper.selectByRecordId(recordId); }
    @Override public int updateRating(Integer recordId, Double rating) { return classRecordMapper.updateRating(recordId, rating); }
    @Override public int updateProgress(Integer recordId, Integer completed, Integer status) { return classRecordMapper.updateProgress(recordId, completed, status); }
    @Override public ClassRecord selectByClassIdAndMemberId(Integer classId, String memberId) { return classRecordMapper.selectByClassIdAndMemberId(classId, memberId); }
    @Override public List<ClassRecordVO> selectByClassId(Integer classId) { return classRecordMapper.selectByClassId(classId); }
    @Override public int countByClassId(Integer classId) { return classRecordMapper.countByClassId(classId); }
    @Override public int countActiveByClassId(Integer classId) { return classRecordMapper.countActiveByClassId(classId); }
    @Override public List<ClassRecordVO> searchRecords(Integer classId, String className, String memberKeyword, Integer coachId,
                                                       Integer status, String ratingStatus, Double lowRating, Double highRating,
                                                       int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        int offset = (safePage - 1) * safePageSize;
        return classRecordMapper.searchRecords(classId, className, memberKeyword, coachId, status, ratingStatus, lowRating, highRating, offset, safePageSize);
    }
    @Override public int countSearchRecords(Integer classId, String className, String memberKeyword, Integer coachId,
                                            Integer status, String ratingStatus, Double lowRating, Double highRating) {
        return classRecordMapper.countSearchRecords(classId, className, memberKeyword, coachId, status, ratingStatus, lowRating, highRating);
    }
    @Override public List<Map<String, Object>> selectRatingView(Double minRating, String gender, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return classRecordMapper.selectRatingView(minRating, gender, safeLimit);
    }
}
