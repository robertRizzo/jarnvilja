package com.jarnvilja.dto;

import java.time.LocalDate;

public class MembershipStatsDTO {

    private Long memberId;
    private int totalBookings;
    private String mostBookedClass;
    private LocalDate memberSince;

    public MembershipStatsDTO(Long memberId, int totalBookings, String mostBookedClass, LocalDate memberSince) {
        this.memberId = memberId;
        this.totalBookings = totalBookings;
        this.mostBookedClass = mostBookedClass;
        this.memberSince = memberSince;
    }

    public Long getMemberId() { return memberId; }
    public int getTotalBookings() { return totalBookings; }
    public String getMostBookedClass() { return mostBookedClass; }
    public LocalDate getMemberSince() { return memberSince; }
}
