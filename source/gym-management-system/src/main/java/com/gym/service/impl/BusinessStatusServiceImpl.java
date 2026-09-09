package com.gym.service.impl;

import com.gym.mapper.BusinessStatusMapper;
import com.gym.pojo.BusinessStatus;
import com.gym.service.BusinessStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessStatusServiceImpl implements BusinessStatusService {

    @Autowired
    private BusinessStatusMapper businessStatusMapper;

    @Override
    public BusinessStatus selectStatus() {
        return businessStatusMapper.selectStatus();
    }

    @Override
    public int updateStatus(Integer status) {
        return businessStatusMapper.updateStatus(status);
    }
}
