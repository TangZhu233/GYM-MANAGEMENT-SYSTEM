package com.gym.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface GymCheckinMapper {

    List<LocalDate> selectCheckDates(@Param("memberId") String memberId);

    int insertCheckin(@Param("memberId") String memberId, @Param("checkDate") LocalDate checkDate);
}
