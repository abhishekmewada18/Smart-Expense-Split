package com.expensesplit.dto;

import java.time.LocalDateTime;

public class GroupMemberResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private LocalDateTime joinedAt;

    public GroupMemberResponse() {
    }

    public GroupMemberResponse(Long id, Long userId, String userName, String userEmail, LocalDateTime joinedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
