package com.expensesplit.controller;

import com.expensesplit.dto.*;
import com.expensesplit.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody GroupCreateRequest request) {
        GroupResponse created = groupService.createGroup(request);
        return new ResponseEntity<>(ApiResponse.success("Group created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();
        return ResponseEntity.ok(ApiResponse.success("Groups retrieved successfully", groups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable Long id) {
        GroupResponse group = groupService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.success("Group details retrieved successfully", group));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> addMember(@PathVariable Long groupId,
                                                                      @Valid @RequestBody AddMemberRequest request) {
        GroupMemberResponse member = groupService.addMemberToGroup(groupId, request);
        return new ResponseEntity<>(ApiResponse.success("Member added to group successfully", member), HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable Long groupId,
                                                          @PathVariable Long userId) {
        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success("Member removed from group successfully", null));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group members retrieved successfully", members));
    }
}

