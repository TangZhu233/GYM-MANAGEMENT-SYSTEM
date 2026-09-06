package com.gym.service;

import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;

import java.util.List;
import java.util.Map;

public interface ClassRecordService {
    List<ClassRecordVO> findAll();
    Boolean insertRecord(ClassRecord record);
    List<ClassRecordVO> selectByMemberId(String memberId);
    Boolean deleteByRecordId(Integer recordId);
    ClassRecord selectByRecordId(Integer recordId);
    int updateRating(Integer recordId, Double rating);
    int updateProgress(Integer recordId, Integer completed, Integer status);
    ClassRecord selectByClassIdAndMemberId(Integer classId, String memberId);
    List<ClassRecordVO> selectByClassId(Integer classId);
    int countByClassId(Integer classId);
    int countActiveByClassId(Integer classId);
    List<ClassRecordVO> searchRecords(Integer classId, String className, String memberKeyword, Integer status,
                                      String ratingStatus, Double lowRating, Double highRating,
                                      int page, int pageSize);
    int countSearchRecords(Integer classId, String className, String memberKeyword, Integer status,
                           String ratingStatus, Double lowRating, Double highRating);
    List<Map<String, Object>> selectRatingView(Double minRating, String gender, int limit);
}
