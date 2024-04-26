package com.example.phase1_fams.controller;

import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.request.UsersReqCreate;
import com.example.phase1_fams.dto.request.UsersReqUpdate;
import com.example.phase1_fams.dto.response.UserAvailable;
import com.example.phase1_fams.service.UsersService;
import com.example.phase1_fams.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.utils.AppConstants;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "REST APIs for Users Management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPPER_ADMIN')")
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService){
        this.usersService = usersService;
    }

    @PreAuthorize("hasAuthority('user:create')")
    @Operation(
            summary = "Add new users",
            description = "Add new users.  Nhớ truyền xuống createdBy 'created bởi ai' "
    )
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @PostMapping()
    public ResponseEntity<?> addNewUser(@RequestBody @Valid UsersReqCreate usersReq){

        UsersDTO savedUsers = usersService.addNewUser(usersReq);
        return ResponseUtils.response(savedUsers, "User created successfully!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get users details",
            description = "Get users details with id (using PathVariable)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        UsersRes usersRes = usersService.getUserById(id);
        return ResponseUtils.response(usersRes, "Get user's details successfully!" , HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:read')")
    @Operation(
            summary = "Get all users or search",
            description = "Get all users or search with paging. Truyền vô page number (default = 0) và page size (default = 10)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("")
    public ResponseEntity<?> searchUsersBySearchKey(@RequestParam(required = false) String searchKey,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                    @RequestParam(required = false) List<String> gender,
                                                    @RequestParam(required = false) List<String> roles,
                                                    @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
                                                    @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size) {
        Page<UsersRes> list = usersService.searchUsersBySearchKey(searchKey, startDate, endDate, gender, roles,  page, size);
        return ResponseUtils.response(list, "Retrieved list succesfully", HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('user:read')")
    @Operation(
            summary = "Search class admins by name",
            description = "Get class admins by search name containing"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/class-admins")
    public ResponseEntity<?> getClassAdminList(@RequestParam(required = false) String name){
        List<UserAvailable> list = usersService.getClassAdminList(name);
        return ResponseUtils.response(list, "Get list of class admin with active status successful", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:read')")
    @Operation(
            summary = "Search trainers by name",
            description = "Get trainers by search name containing"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @GetMapping("/trainers")
    public ResponseEntity<?> getTrainerList(@RequestParam(required = false) String name){
        List<UserAvailable> list = usersService.getTrainerList(name);
        return ResponseUtils.response(list, "Get list of class admin with active status successful", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @Operation(
            summary = "Update user's status",
            description = "Update users status by id. Chỉ cần truyền vào ID là sẽ đổi từ status hiện tại sang status còn lại(Inactive/Active)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping("/status/{userId}")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable Long userId) {

            return ResponseUtils.response(usersService.changeUserStatus(userId), "User status changed successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:update')")
    @Operation(
            summary = "Update user's role",
            description = "Update user's role by id. \n" +
                    "roleId: 1 (SUPER_ADMIN) / 2 (CLASS_ADMIN) / 3 (TRAINER)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestParam Integer newRoleId) {

            usersService.updateUserRoleById(userId, newRoleId);
            return ResponseUtils.response(usersService.updateUserRoleById(userId, newRoleId), "User role updated successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:update')")
    @Operation(
            summary = "Update users information",
            description = "Update users information by ID. Nhớ truyền xuống modifiedBy 'update bởi ai' \n" +
                        "Gender: Male/Female/Others \n" +
                        "Status: 1 (Active) / 0 (Inactive)"
    )
    @ApiResponse(responseCode = "200", description = "Http Status 200 SUCCESS")
    @PutMapping("/information/{id}")
    public ResponseEntity<?> updateUserInformation(@PathVariable("id") Long id, @RequestBody @Valid UsersReqUpdate usersDTO){

            return ResponseUtils.response(usersService.updateUserInformation(id, usersDTO), "User information updated successfully", HttpStatus.OK);
    }
}
