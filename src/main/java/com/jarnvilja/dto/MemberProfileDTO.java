package com.jarnvilja.dto;

public class MemberProfileDTO {

    private Long id;
    private String username;
    private String email;

    public MemberProfileDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

}
