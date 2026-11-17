package com.expensesplit.service;

import com.expensesplit.dto.UserCreateRequest;
import com.expensesplit.dto.UserResponse;
import com.expensesplit.dto.UserUpdateRequest;
import com.expensesplit.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    User getUserEntityById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}

