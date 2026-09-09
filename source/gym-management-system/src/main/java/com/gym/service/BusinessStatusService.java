package com.gym.service;

import com.gym.pojo.BusinessStatus;

public interface BusinessStatusService {

    BusinessStatus selectStatus();

    int updateStatus(Integer status);
}
