package com.expensesplit.service;

import com.expensesplit.dto.AddMemberRequest;
import com.expensesplit.dto.GroupCreateRequest;
import com.expensesplit.dto.GroupMemberResponse;
import com.expensesplit.dto.GroupResponse;
import com.expensesplit.entity.ExpenseGroup;
import com.expensesplit.entity.User;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupCreateRequest request);
    List<GroupResponse> getAllGroups();
    GroupResponse getGroupById(Long id);
    ExpenseGroup getGroupEntityById(Long id);
    GroupMemberResponse addMemberToGroup(Long groupId, AddMemberRequest request);
    void removeMemberFromGroup(Long groupId, Long userId);
    List<GroupMemberResponse> getGroupMembers(Long groupId);
    List<User> getGroupMemberEntities(Long groupId);
}

