package com.gym.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusinessStatus {
    private Integer id;
    private Integer status;
    private LocalDateTime updateTime;
}
