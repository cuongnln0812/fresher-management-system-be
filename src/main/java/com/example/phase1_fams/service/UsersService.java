package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.response.UserAvailable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.request.UsersReqCreate;
import com.example.phase1_fams.dto.request.UsersReqUpdate;
import com.example.phase1_fams.dto.response.UsersRes;

import java.time.LocalDate;
import java.util.List;

@Service
public interface UsersService {

    UsersDTO addNewUser(UsersReqCreate usersReq);

    Page<UsersRes> searchUsersBySearchKey(String searchKey, LocalDate startDate, LocalDate endDate,
                                          List<String> genders, List<String> roles, int page, int size);

    UsersRes getUserById(Long id);

    UsersRes updateUserRoleById(Long userId, int newRoleId);

    UsersRes changeUserStatus(Long userId);

    Page<UsersRes> getAllUserPage(int page, int size);

    UsersDTO updateUserInformation(Long id, UsersReqUpdate usersDTO);

    List<UserAvailable> getClassAdminList(String name);

    List<UserAvailable> getTrainerList(String name);

}
