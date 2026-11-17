package com.expensesplit.service.impl;

import com.expensesplit.dto.AddMemberRequest;
import com.expensesplit.dto.GroupCreateRequest;
import com.expensesplit.dto.GroupMemberResponse;
import com.expensesplit.dto.GroupResponse;
import com.expensesplit.entity.ExpenseGroup;
import com.expensesplit.entity.GroupMember;
import com.expensesplit.entity.User;
import com.expensesplit.exception.BadRequestException;
import com.expensesplit.exception.ResourceNotFoundException;
import com.expensesplit.repository.ExpenseGroupRepository;
import com.expensesplit.repository.GroupMemberRepository;
import com.expensesplit.service.GroupService;
import com.expensesplit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserService userService;

    public GroupServiceImpl(ExpenseGroupRepository groupRepository,
                            GroupMemberRepository groupMemberRepository,
                            UserService userService) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userService = userService;
    }

    @Override
    public GroupResponse createGroup(GroupCreateRequest request) {
        User creator = userService.getUserEntityById(request.getCreatedById());

        ExpenseGroup group = new ExpenseGroup();
        group.setName(request.getName().trim());
        group.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        group.setCreatedBy(creator);

        ExpenseGroup savedGroup = groupRepository.save(group);

        // Add creator as member automatically
        GroupMember creatorMember = new GroupMember(savedGroup, creator);
        groupMemberRepository.save(creatorMember);

        // Add other initial members if provided
        if (request.getInitialMemberUserIds() != null) {
            for (Long memberUserId : request.getInitialMemberUserIds()) {
                if (!memberUserId.equals(creator.getId())) {
                    User memberUser = userService.getUserEntityById(memberUserId);
                    if (!groupMemberRepository.existsByGroupIdAndUserId(savedGroup.getId(), memberUser.getId())) {
                        GroupMember member = new GroupMember(savedGroup, memberUser);
                        groupMemberRepository.save(member);
                    }
                }
            }
        }

        return getGroupById(savedGroup.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        List<ExpenseGroup> groups = groupRepository.findAll();
        List<GroupResponse> responses = new ArrayList<>();
        for (ExpenseGroup group : groups) {
            responses.add(mapToResponse(group));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        ExpenseGroup group = getGroupEntityById(id);
        return mapToResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseGroup getGroupEntityById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    @Override
    public GroupMemberResponse addMemberToGroup(Long groupId, AddMemberRequest request) {
        ExpenseGroup group = getGroupEntityById(groupId);
        User user = userService.getUserEntityById(request.getUserId());

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, user.getId())) {
            throw new BadRequestException("User '" + user.getName() + "' is already a member of this group");
        }

        GroupMember groupMember = new GroupMember(group, user);
        GroupMember saved = groupMemberRepository.save(groupMember);

        return new GroupMemberResponse(
                saved.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                saved.getJoinedAt()
        );
    }

    @Override
    public void removeMemberFromGroup(Long groupId, Long userId) {
        getGroupEntityById(groupId); // Validate group exists
        userService.getUserEntityById(userId); // Validate user exists

        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ResourceNotFoundException("User id " + userId + " is not a member of group " + groupId);
        }

        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        getGroupEntityById(groupId);
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<GroupMemberResponse> responses = new ArrayList<>();
        for (GroupMember member : members) {
            responses.add(new GroupMemberResponse(
                    member.getId(),
                    member.getUser().getId(),
                    member.getUser().getName(),
                    member.getUser().getEmail(),
                    member.getJoinedAt()
            ));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getGroupMemberEntities(Long groupId) {
        getGroupEntityById(groupId);
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<User> users = new ArrayList<>();
        for (GroupMember member : members) {
            users.add(member.getUser());
        }
        return users;
    }

    private GroupResponse mapToResponse(ExpenseGroup group) {
        List<GroupMember> members = groupMemberRepository.findByGroupId(group.getId());
        List<GroupMemberResponse> memberResponses = new ArrayList<>();
        for (GroupMember gm : members) {
            memberResponses.add(new GroupMemberResponse(
                    gm.getId(),
                    gm.getUser().getId(),
                    gm.getUser().getName(),
                    gm.getUser().getEmail(),
                    gm.getJoinedAt()
            ));
        }

        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedBy().getId(),
                group.getCreatedBy().getName(),
                memberResponses,
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }
}

