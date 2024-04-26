package com.example.phase1_fams.service;

import com.example.phase1_fams.auth.AuthenticationRequest;
import com.example.phase1_fams.auth.AuthenticationResponse;
import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.UserAuthRes;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTests {
    @Mock
    private UsersRepository repository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationService authenticationService;

    Users testUser;
    Role testRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void init(){
        testUser = Users.builder()
                .id(1L)
                .email("mockuser@gmail.com")
                .name("MockUser")
                .dob(LocalDate.of(2000, 1, 1))
                .phone("0911222333")
                .gender("Male")
                .build();
        testRole = Role.builder()
                .roleId(1)
                .roleName("Admin")
                .classPermissionGroup(ClassPermissionGroup.FULL_ACCESS)
                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.FULL_ACCESS)
                .syllabusPermissionGroup(SyllabusPermissionGroup.FULL_ACCESS)
                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.FULL_ACCESS)
                .userPermissionGroup(UserPermissionGroup.FULL_ACCESS)
                .build();
    }

    @Test
    void testAuthenticate_Success() {
        //Dummy Data
        String email = "mockuser@gmail.com";
        String pass = "mockpass123";
        String sampleToken = "sampletoken123";
        Role testRole = new Role();
        AuthenticationRequest request = new AuthenticationRequest(email, pass);

        testUser.setRole(testRole);
        testUser.setStatus(true); // Set as active user

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn(sampleToken);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals(sampleToken, response.getToken());
        verify(jwtService).generateToken(testUser);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

    }

    @Test
    public void testAuthenticate_UserNotFound() {
        String email = "wrongemail@gmail.com";
        String pass = "mockpass123";
        AuthenticationRequest request = new AuthenticationRequest(email, pass);

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("User cannot found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void testAuthenticate_UserIsForbidden() {
        String email = "mockuser@gmail.com";
        String pass = "mockpass123";
        AuthenticationRequest request = new AuthenticationRequest(email, pass);
        testUser.setStatus(false); // Set as inactive user

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        ApiException exception = assertThrows(ApiException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals("User is forbidden!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }



    @Test
    void testGetUserInfo_Successful() {
        //data
        String email = "mockuser@gmail.com";

        testUser.setRole(testRole);
        testUser.setStatus(true);

        //Arrange
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(testUser));

        //Method
        UserAuthRes userAuthRes = authenticationService.getUserInfo();

        //Assert
        assertNotNull(userAuthRes);
        assertEquals(testUser.getEmail(), userAuthRes.getEmail());
        assertEquals(testUser.getName(), userAuthRes.getName());
        assertEquals(testUser.getDob(), userAuthRes.getDob());
        assertEquals(testUser.getPhone(), userAuthRes.getPhone());
        assertEquals(testUser.getGender(), userAuthRes.getGender());
        assertEquals(testUser.getRole().getRoleName(), userAuthRes.getRoleName());
        assertEquals(String.valueOf(testUser.getRole().getSyllabusPermissionGroup()), userAuthRes.getSyllabusPermission());
        assertEquals(String.valueOf(testUser.getRole().getTrainingProgramPermissionGroup()), userAuthRes.getTrainingProgramPermission());
        assertEquals(String.valueOf(testUser.getRole().getClassPermissionGroup()), userAuthRes.getClassPermission());
        assertEquals(String.valueOf(testUser.getRole().getLearningMaterialPermissionGroup()), userAuthRes.getLearningMaterialPermission());
        verify(repository).findByEmail(email);
    }

    @Test
    public void testGetUserInfo_UserNotFound() {
        String email = "invalid@gmail.com";

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            authenticationService.getUserInfo();
        });

        assertEquals("User cannot found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetName_Successful() {
        String email = "mockuser@gmail.com";

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(testUser));

        String result = authenticationService.getName();

        assertEquals(testUser.getName(), result);

        verify(repository).findByEmail(email);
    }

    @Test
    public void testGetName_UserNotFound() {
        String email = "invalid@gmail.com";

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            authenticationService.getName();
        });

        assertEquals("Cannot find who is logged in", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testChangePassword_Successful() {
        String email = "mockuser@gmail.com";
        String oldPassword = "oldPass";
        String newPassword = "NewPass123@";
        String newEncryptedPassword = "newEPass";

        testUser.setPassword("oldEncryptedPass");
        testUser.setFirstLogin(false);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newEncryptedPassword);

        authenticationService.changePassword(oldPassword, newPassword);


        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(repository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertNotNull(savedUser);
        verify(passwordEncoder).encode(newPassword); // Ensure newPassword was encoded
        assertEquals(newEncryptedPassword, savedUser.getPassword()); // This assumes you can directly get the password
        assertFalse(savedUser.isFirstLogin()); // Example if setting first login status was part of the logic
    }

    @Test
    void testChangePasswordFirstLoginTrue_Successful() {
        String email = "mockuser@gmail.com";
        String oldPassword = "oldPass";
        String newPassword = "NewPass123@";
        String newEncryptedPassword = "newEPass";

        testUser.setPassword("oldEncryptedPass");
        testUser.setFirstLogin(true);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newEncryptedPassword);

        authenticationService.changePassword(oldPassword, newPassword);


        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(repository).save(userCaptor.capture());
        Users savedUser = userCaptor.getValue();

        assertNotNull(savedUser);
        verify(passwordEncoder).encode(newPassword); // Ensure newPassword was encoded
        assertEquals(newEncryptedPassword, savedUser.getPassword()); // This assumes you can directly get the password
        assertFalse(savedUser.isFirstLogin()); // Example if setting first login status was part of the logic
    }

    @Test
    void testChangePassword_UserNotFound() {
        String username = "invalid@example.com";
        String oldPassword = "oldPassword";

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(repository.findByEmail(username)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                authenticationService.changePassword(oldPassword, "newPassword"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User cannot found!", exception.getMessage());
    }

    @Test
    void testChangePassword_RegexNotMatch() {
        String username = "invalid@example.com";
        String oldPassword = "oldPassword";
        Users user = mock(Users.class);
        user.setPassword("oldEncryptedPassword");

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(repository.findByEmail(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () ->
                authenticationService.changePassword(oldPassword, "newPassword"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Password must be 8-12 characters with at least one uppercase letter, " +
                        "one number, and one special character (!@#$%^&*)."
                , exception.getMessage());
    }

    @Test
    void testChangePassword_OldPasswordNotMatch() {
        String username = "mockuser@example.com";
        String oldPassword = "wrongPassword";
        Users user = mock(Users.class);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(username);
        when(repository.findByEmail(username)).thenReturn(Optional.of(user));
        when(user.getPassword()).thenReturn(oldPassword);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () ->
                authenticationService.changePassword(oldPassword, "newPassword"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Old password does not match!", exception.getMessage());
    }
}