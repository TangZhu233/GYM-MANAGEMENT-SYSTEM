package com.gym.mapper;

import com.gym.pojo.BusinessStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BusinessStatusMapper {

    BusinessStatus selectStatus();

    int updateStatus(@Param("status") Integer status);
}
