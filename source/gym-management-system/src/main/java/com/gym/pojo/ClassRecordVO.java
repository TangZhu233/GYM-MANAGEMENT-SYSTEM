package com.gym.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class ClassRecordVO {
    private Integer recordId;
    private String memberId;
    private String memberName;
    private Integer classId;
    private Integer coachId;
    private String className;
    private String coachName;
    private String classBegin;
    private Date joinTime;
    private Integer status;
    private Integer totalSessions;
    private Integer completedSessions;
    private Double rating;
}
