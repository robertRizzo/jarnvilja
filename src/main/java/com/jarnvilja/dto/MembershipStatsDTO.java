package com.jarnvilja.dto;

public class MembershipStatsDTO {

    private Long memberId;
    private int totalBookings;

    public MembershipStatsDTO(Long memberId, int totalBookings) {
        this.memberId = memberId;
        this.totalBookings = totalBookings;
    }

    public Long getMemberId() { return memberId; }
    public int getTotalBookings() { return totalBookings; }
}
