package com.gym.service;

import com.gym.pojo.Member;

import java.util.Map;

public interface HealthService {

    /** 今日已打卡返回 false，否则插入成功返回 true。 */
    boolean checkIn(String memberId);

    /** 组装健康评估首页数据：打卡摘要 + BMI（可为空）+ 饮食推荐（按 BMI，空则 null）。 */
    Map<String, Object> overview(Member member);
}
