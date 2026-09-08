package com.gym.mapper;

import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassRecordMapper {

    List<ClassRecordVO> findAll();

    Boolean insertRecord(ClassRecord record);

    List<ClassRecordVO> selectByMemberId(String memberId);

    Boolean deleteByRecordId(Integer recordId);

    ClassRecord selectByRecordId(Integer recordId);

    int updateRating(@Param("recordId") Integer recordId, @Param("rating") Double rating);

    int updateProgress(@Param("recordId") Integer recordId,
                       @Param("completed") Integer completed,
                       @Param("status") Integer status);

    ClassRecord selectByClassIdAndMemberId(Integer classId, String memberId);

    List<ClassRecordVO> selectByClassId(Integer classId);

    int countByClassId(Integer classId);

    int countActiveByClassId(Integer classId);

    List<ClassRecordVO> searchRecords(@Param("classId") Integer classId,
                                      @Param("className") String className,
                                      @Param("memberKeyword") String memberKeyword,
                                      @Param("coachId") Integer coachId,
                                      @Param("status") Integer status,
                                      @Param("ratingStatus") String ratingStatus,
                                      @Param("lowRating") Double lowRating,
                                      @Param("highRating") Double highRating,
                                      @Param("offset") int offset,
                                      @Param("pageSize") int pageSize);

    int countSearchRecords(@Param("classId") Integer classId,
                           @Param("className") String className,
                           @Param("memberKeyword") String memberKeyword,
                           @Param("coachId") Integer coachId,
                           @Param("status") Integer status,
                           @Param("ratingStatus") String ratingStatus,
                           @Param("lowRating") Double lowRating,
                           @Param("highRating") Double highRating);

    List<Map<String, Object>> selectRatingView(@Param("minRating") Double minRating,
                                               @Param("gender") String gender,
                                               @Param("limit") int limit);
}
