package com.expensesplit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GroupCreateRequest {

    @NotBlank(message = "Group name is required")
    @Size(min = 2, max = 100, message = "Group name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "CreatedBy user ID is required")
    private Long createdById;

    private List<Long> initialMemberUserIds;

    public GroupCreateRequest() {
    }

    public GroupCreateRequest(String name, String description, Long createdById, List<Long> initialMemberUserIds) {
        this.name = name;
        this.description = description;
        this.createdById = createdById;
        this.initialMemberUserIds = initialMemberUserIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public List<Long> getInitialMemberUserIds() {
        return initialMemberUserIds;
    }

    public void setInitialMemberUserIds(List<Long> initialMemberUserIds) {
        this.initialMemberUserIds = initialMemberUserIds;
    }
}
