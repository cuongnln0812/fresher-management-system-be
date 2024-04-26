package com.example.phase1_fams.service.impl;

import static com.example.phase1_fams.utils.AppConstants.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.phase1_fams.dto.response.UserAvailable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.UsersConverter;
import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.UsersReqCreate;
import com.example.phase1_fams.dto.request.UsersReqUpdate;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.model.Role;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.RoleRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.UsersService;
import com.example.phase1_fams.utils.AutomaticGeneratedPassword;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UsersConverter usersConverter;
    private final AuthenticationService authenticationService;
    private final JavaMailSender mailSender;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, UsersConverter usersConverter, ModelMapper modelMapper,
            AuthenticationService authenticationService, JavaMailSender mailSender) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersConverter = usersConverter;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
        this.authenticationService = authenticationService;
    }

    @Override
    public UsersDTO addNewUser(UsersReqCreate usersReq) {
        // Assuming your Role entity has a constructor that takes roleId
        if (!isEmailUnique(usersReq.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already in use");
        } else if (!usersReq.getEmail().matches(EMAIL_REGEX)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is not valid");
        }
        if(usersReq.getDob().isAfter(LocalDate.now()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dob cannot be after present date!");
        Role role = roleRepository.findById(usersReq.getRoleId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Role not found"));

        Users newUser = new Users();
        String randomPassword = AutomaticGeneratedPassword.generateRandomPassword();
        newUser.setName(usersReq.getName());
        newUser.setEmail(usersReq.getEmail());
        newUser.setPassword(passwordEncoder.encode(randomPassword));
        newUser.setPhone(usersReq.getPhone());
        newUser.setDob(usersReq.getDob());
        newUser.setGender(usersReq.getGender());
        newUser.setStatus(usersReq.isStatus());
        newUser.setCreatedDate();
        newUser.setCreatedBy(authenticationService.getName());
        newUser.setRole(role);
        newUser.setFirstLogin(true);
        Users saved = usersRepository.save(newUser);

        // Construct the email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("\"FAMS\" <fams.automatic.noreply@gmail.com>");
        message.setTo(saved.getEmail());
        // Set a meaningful message
        message.setSubject("[FAMS] - Tài khoản được tạo thành công");
        message.setText("Hi, " + saved.getName() + ",\n\n" +
                "Tài khoản đăng nhập vào hệ thống FAMS của bạn đã được tạo thành công.\n" +
                "Vui lòng truy cập hệ thống theo thông tin sau:\n" +
                "• Username: " + saved.getEmail() + "\n" +
                "• Password: " + randomPassword + "\n" + // Placeholder for the password
                "Lưu ý: Vui lòng thay đổi mật khẩu sau khi đăng nhập.\n");

        // Send the email (assuming you have a mailSender bean configured)
        mailSender.send(message);

        return usersConverter.toDTO(saved);
    }

    private boolean isEmailUnique(String email) {
        Optional<Users> existingUser = usersRepository.findByEmail(email);
        return existingUser.isEmpty();
    }

    @Override
    @Transactional
    public UsersDTO updateUserInformation(Long id, UsersReqUpdate usersDTO) {
        Users existingUser = usersRepository.findById(Long.parseLong(String.valueOf(id)))
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(usersDTO.getName());
        existingUser.setPhone(usersDTO.getPhone());
        existingUser.setDob(usersDTO.getDob());
        existingUser.setGender(usersDTO.getGender());
        existingUser.setStatus(usersDTO.isStatus());
        existingUser.setModifiedDate();
        existingUser.setModifiedBy(authenticationService.getName());
        Users user = usersRepository.save(existingUser);
        return usersConverter.toDTO(user);
    }

    @Override
    public Page<UsersRes> searchUsersBySearchKey(String searchKey, LocalDate startDate, LocalDate endDate, List<String> gender, List<String> roles, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> usersPage;
        if(startDate != null) {
            if(endDate != null) usersPage = usersRepository.searchByDateNotNull(searchKey, startDate, endDate, gender, roles, pageable);
            else throw new ApiException(HttpStatus.BAD_REQUEST, "End Date must not be null or empty");
        }else{
            if(endDate != null)  throw new ApiException(HttpStatus.BAD_REQUEST, "Start Date must not be null or empty");
            else usersPage = usersRepository.searchByDateNull(searchKey, gender, roles, pageable);
        }
        return usersPage.map(usersConverter::toRes);
    }

    @Override
    public UsersRes getUserById(Long id) {
        Users u = usersRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return usersConverter.toRes(u);
    }

    @Override
    public List<UserAvailable> getClassAdminList(String name){
        List<Users> classAdminList;
        if(name == null){
            classAdminList = usersRepository.findAllByRole_RoleIdAndStatus(2, true);
        }else {
            classAdminList = usersRepository.findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(2, true, name);
        }
        return classAdminList.stream().map(usersConverter::toAvailableUser).toList();
    }

    @Override
    public List<UserAvailable> getTrainerList(String name){
        List<Users> classAdminList;
        if(name == null){
            classAdminList = usersRepository.findAllByRole_RoleIdAndStatus(3, true);
        }else {
            classAdminList = usersRepository.findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(3, true, name);
        }
        return classAdminList.stream().map(usersConverter::toAvailableUser).toList();
    }

    @Override
    @Transactional
    public UsersRes updateUserRoleById(Long userId, int newRoleId) {
        // Load the User instance you want to update
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find user!!!"));

        // Load the RolePermission instance associated with the new roleID
        Role role = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find role!!!"));
        user.setRole(role);
        usersRepository.save(user);
        return usersConverter.toRes(user);
    }

    @Override
    public UsersRes changeUserStatus(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cannot find user!!!"));
        if(user.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())
                || user.getEmail().equals("masteradmin@fpt.com"))
            throw new ApiException(HttpStatus.CONFLICT, "You cannot de-activate this account!!!");
        user.setStatus(!user.isStatus());
        Users userT = usersRepository.save(user);
        return usersConverter.toRes(userT);
    }

    @Override
    public Page<UsersRes> getAllUserPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> userPage = usersRepository.findAll(pageable);
        return userPage.map(usersConverter::toRes);
    }

}