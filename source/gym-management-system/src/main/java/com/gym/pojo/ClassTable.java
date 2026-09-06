package com.gym.pojo;

import lombok.Data;

@Data
public class ClassTable {
    private Integer classId;
    private String className;
    private String classBegin;
    private String classTime;
    private String coach;
    private String coachId;
    private Integer classLevel;
    private Integer classDuration;
    private Integer maxCapacity;
    private Integer totalSessions;
    private String classDescription;
    private Double price;
    private Integer enrolledCount;
    private Double capacityUsage;
    private Double averageRating;
    private Integer ratingCount;
}
