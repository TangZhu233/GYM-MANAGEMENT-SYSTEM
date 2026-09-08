package com.gym.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class ClassRecord {
    private Integer recordId;
    private String memberId;
    private Integer classId;
    private Integer coachId;
    private Date joinTime;
    private Integer status;
    private Integer totalSessions;
    private Integer completedSessions;
    private Double rating;
}
