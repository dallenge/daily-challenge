package com.example.dailychallenge.entity.badge.type;

import java.util.Arrays;
import java.util.Optional;

// 챌린지 N개 달성
public enum AchievementBadgeType implements BadgeType{

    ACHIEVED_10(10),
    ACHIEVED_20(20),
    ACHIEVED_30(30),
    ACHIEVED_40(40),
    ACHIEVED_50(50),
    ;

    private final int number;

    AchievementBadgeType(int number) {
        this.number = number;
    }

    public static Optional<AchievementBadgeType> findByNumber(int number) {
        return Arrays.stream(values())
                .filter(badgeType -> badgeType.isSameNumber(number))
                .findAny();
    }

    @Override
    public boolean isSameNumber(int number) {
        return this.number == number;
    }

    @Override
    public String getName() {
        return String.format("챌린지 %d개 달성", this.number);
    }
}
