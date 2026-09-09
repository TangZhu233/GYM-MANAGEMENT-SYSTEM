package com.gym.service.impl;

import com.gym.mapper.GymCheckinMapper;
import com.gym.pojo.Member;
import com.gym.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class HealthServiceImpl implements HealthService {

    @Autowired
    private GymCheckinMapper checkinMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean checkIn(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (checkinMapper.selectCheckDates(memberId).contains(today)) {
            return false;
        }
        return checkinMapper.insertCheckin(memberId, today) == 1;
    }

    @Override
    public Map<String, Object> overview(Member member) {
        Map<String, Object> resp = new LinkedHashMap<>();

        List<LocalDate> dates = new ArrayList<>(checkinMapper.selectCheckDates(member.getMemberAccount()));
        resp.put("checkin", checkinSummary(dates));

        Map<String, Object> memberBrief = new LinkedHashMap<>();
        memberBrief.put("memberName", member.getMemberName());
        memberBrief.put("memberHeight", member.getMemberHeight());
        memberBrief.put("memberWeight", member.getMemberWeight());
        resp.put("member", memberBrief);

        Map<String, Object> bmi = bmiOf(member);
        resp.put("bmi", bmi);
        resp.put("diet", bmi == null ? null
                : dietOf((String) bmi.get("gradeCode"), member.getMemberWeight()));
        return resp;
    }

    // ===== 打卡摘要：累计 / 今日是否已签 / 连续天数 / 本月已签日期 =====
    private Map<String, Object> checkinSummary(List<LocalDate> dates) {
        Map<String, Object> summary = new LinkedHashMap<>();
        Set<LocalDate> set = new HashSet<>(dates);
        LocalDate today = LocalDate.now();

        summary.put("total", set.size());
        summary.put("today", set.contains(today));

        // 连续天数：今天已签从今天起算，未签从昨天起算
        int streak = 0;
        LocalDate cursor = set.contains(today) ? today : today.minusDays(1);
        while (cursor != null && set.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        summary.put("streak", streak);

        List<String> monthDates = new ArrayList<>();
        for (LocalDate d : dates) {
            if (d.getYear() == today.getYear() && d.getMonth() == today.getMonth()) {
                monthDates.add(d.format(DATE_FMT));
            }
        }
        summary.put("monthDates", monthDates);
        return summary;
    }

    // ===== BMI 分级 =====
    private Map<String, Object> bmiOf(Member member) {
        Integer height = member.getMemberHeight();
        Integer weight = member.getMemberWeight();
        if (height == null || weight == null || height <= 0 || weight <= 0) {
            return null;
        }
        double meter = height / 100.0;
        double value = weight / (meter * meter);
        double rounded = Math.round(value * 10) / 10.0;

        String gradeCode;
        String label;
        String advice;
        if (rounded < 18.5) {
            gradeCode = "thin";
            label = "偏瘦";
            advice = "适当增肌：热量略高于消耗，规律力量训练，配合充足蛋白质与睡眠。";
        } else if (rounded < 24) {
            gradeCode = "normal";
            label = "正常";
            advice = "保持现状：均衡饮食 + 规律训练，注意睡眠与恢复即可。";
        } else if (rounded < 28) {
            gradeCode = "over";
            label = "偏胖";
            advice = "适度减脂：增加有氧与力量训练，控制精制碳水和油脂摄入，循序渐进。";
        } else {
            gradeCode = "fat";
            label = "肥胖";
            advice = "科学减重：建议低热量饮食 + 循序渐进的运动，必要时咨询专业人士。";
        }

        Map<String, Object> bmi = new LinkedHashMap<>();
        bmi.put("value", rounded);
        bmi.put("gradeCode", gradeCode);
        bmi.put("label", label);
        bmi.put("advice", advice);
        return bmi;
    }

    // ===== 饮食推荐（纯静态规则 + 按体重估全天热量）=====
    private Map<String, Object> dietOf(String gradeCode, Integer weightKg) {
        int w = weightKg == null || weightKg <= 0 ? 60 : weightKg;
        int factor;
        switch (gradeCode == null ? "" : gradeCode) {
            case "thin": factor = 34; break;
            case "over": factor = 26; break;
            case "fat": factor = 22; break;
            default: factor = 30; break;
        }
        int dailyKcal = ((w * factor + 25) / 50) * 50;

        switch (gradeCode == null ? "" : gradeCode) {
            case "thin":
                return buildDiet("高蛋白增肌 · 目标体脂上升", dailyKcal,
                        Arrays.asList(
                                meal("早餐", "鸡蛋 2 个 + 全麦面包 + 牛奶", "约 450 千卡"),
                                meal("午餐", "鸡胸肉 150g + 米饭 + 西兰花", "约 600 千卡"),
                                meal("晚餐", "牛肉 / 三文鱼 + 紫薯 + 蔬菜沙拉", "约 550 千卡"),
                                meal("加餐", "香蕉 / 坚果一小把 / 蛋白奶昔", "约 200 千卡")),
                        Arrays.asList("油炸零食", "酒精"),
                        Arrays.asList("全天热量比消耗多 300~500 千卡、缓慢增重", "训练后 1 小时内补蛋白最有效", "每周体重 +0.25~0.5kg 属健康增速"));
            case "over":
                return buildDiet("低脂减脂 · 目标缓慢掉秤", dailyKcal,
                        Arrays.asList(
                                meal("早餐", "全麦吐司 + 鸡蛋 + 无糖豆浆", "约 380 千卡"),
                                meal("午餐", "糙米饭少量 + 鸡胸肉 + 大量时蔬", "约 500 千卡"),
                                meal("晚餐", "清蒸鱼 + 绿叶蔬菜 + 少量玉米", "约 400 千卡"),
                                meal("加餐", "无糖酸奶 / 小番茄 / 黄瓜", "约 100 千卡")),
                        Arrays.asList("油炸食品", "含糖饮料", "睡前夜宵"),
                        Arrays.asList("每餐先吃蔬菜增加饱腹感", "控油盐，主食粗细搭配", "每周减 0.5kg 以内更可持续"));
            case "fat":
                return buildDiet("严格控卡 · 配合运动减重", dailyKcal,
                        Arrays.asList(
                                meal("早餐", "水煮蛋 1 个 + 蔬菜 + 无糖饮品", "约 300 千卡"),
                                meal("午餐", "少油高蛋白（鸡胸 / 鱼）+ 粗粮少量 + 蔬菜", "约 420 千卡"),
                                meal("晚餐", "轻食：豆腐 + 大量蔬菜 + 低糖水果", "约 350 千卡"),
                                meal("加餐", "小番茄 / 黄瓜 / 少量蓝莓", "约 60 千卡")),
                        Arrays.asList("一切油炸 / 高糖", "酒类", "深夜加餐"),
                        Arrays.asList("记录每日热量，控制在参考值以下", "每周保持 3~5 次有氧，循序渐进", "多喝水（每天 1.5~2L），戒含糖饮料"));
            case "normal":
            default:
                return buildDiet("均衡膳食（碳水 : 蛋白 : 脂肪 ≈ 5 : 3 : 2）", dailyKcal,
                        Arrays.asList(
                                meal("早餐", "燕麦牛奶 + 水煮蛋 + 水果", "约 420 千卡"),
                                meal("午餐", "瘦肉 + 杂粮饭 + 时蔬", "约 560 千卡"),
                                meal("晚餐", "鱼类 / 豆腐 + 蔬菜 + 少量薯类", "约 450 千卡"),
                                meal("加餐", "水果 + 无糖酸奶 / 坚果少量", "约 150 千卡")),
                        Arrays.asList("高糖饮料"),
                        Arrays.asList("三餐规律、每餐七八分饱、多喝水", "蔬菜水果约占每餐一半", "每天运动 30 分钟以上，保持状态"));
        }
    }

    private Map<String, Object> buildDiet(String target, int dailyKcal,
                                          List<Map<String, String>> meals,
                                          List<String> avoid, List<String> tips) {
        Map<String, Object> diet = new LinkedHashMap<>();
        diet.put("target", target);
        diet.put("dailyKcal", dailyKcal);
        diet.put("meals", meals);
        diet.put("avoid", avoid);
        diet.put("tips", tips);
        return diet;
    }

    private Map<String, String> meal(String name, String foods, String kcal) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("foods", foods);
        m.put("kcal", kcal);
        return m;
    }
}
