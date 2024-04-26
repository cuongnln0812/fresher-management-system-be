package com.example.phase1_fams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import com.example.phase1_fams.dto.response.UserAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.phase1_fams.auth.AuthenticationService;
import com.example.phase1_fams.converter.UsersConverter;
import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.request.UsersReqCreate;
import com.example.phase1_fams.dto.request.UsersReqUpdate;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.model.ClassPermissionGroup;
import com.example.phase1_fams.model.LearningMaterialPermissionGroup;
import com.example.phase1_fams.model.Role;
import com.example.phase1_fams.model.SyllabusPermissionGroup;
import com.example.phase1_fams.model.TrainingProgramPermissionGroup;
import com.example.phase1_fams.model.UserPermissionGroup;
import com.example.phase1_fams.model.Users;
import com.example.phase1_fams.repository.RoleRepository;
import com.example.phase1_fams.repository.UsersRepository;
import com.example.phase1_fams.service.impl.UsersServiceImpl;

public class UsersServiceTests {

        @Mock
        private UsersRepository usersRepository;

        @Mock
        private RoleRepository roleRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private UsersConverter usersConverter;

        @Mock
        private AuthenticationService authenticationService;

        @Mock
        private JavaMailSender mailSender;

        @InjectMocks
        private UsersServiceImpl usersService;

        Role admin;
        Role classAdmin;
        Role trainer;

        @BeforeEach
        public void setUp() {
            MockitoAnnotations.openMocks(this);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        }

        @BeforeEach
        void init() {
                admin = Role.builder()
                                .roleId(1)
                                .roleName("SUPPER_ADMIN")
                                .syllabusPermissionGroup(SyllabusPermissionGroup.FULL_ACCESS)
                                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.FULL_ACCESS)
                                .classPermissionGroup(ClassPermissionGroup.FULL_ACCESS)
                                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.FULL_ACCESS)
                                .userPermissionGroup(UserPermissionGroup.FULL_ACCESS)
                                .build();

                classAdmin = Role.builder()
                                .roleId(2)
                                .roleName("CLASS_ADMIN")
                                .syllabusPermissionGroup(SyllabusPermissionGroup.MODIFY)
                                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.VIEW)
                                .classPermissionGroup(ClassPermissionGroup.FULL_ACCESS)
                                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.VIEW)
                                .userPermissionGroup(UserPermissionGroup.CREATE)
                                .build();

                trainer = Role.builder()
                                .roleId(3)
                                .roleName("TRAINER")
                                .syllabusPermissionGroup(SyllabusPermissionGroup.MODIFY)
                                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.VIEW)
                                .classPermissionGroup(ClassPermissionGroup.MODIFY)
                                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.VIEW)
                                .userPermissionGroup(UserPermissionGroup.NO_ACCESS)
                                .build();
        }

        @Test
        public void testUpdateUserRoleClassAdminById() {
                // Prepare test data
                Users user = Users.builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .role(admin)
                                .build();
                Long userId = user.getId();
                int newRoleID = classAdmin.getRoleId();
                UsersRes userRes = new UsersRes();
                // Mock the role repository to return the new role when findById is called
                when(roleRepository.findById(newRoleID)).thenReturn(Optional.of(classAdmin));
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(UsersRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .roleName(classAdmin.getRoleName())
                                .build());

                // Mock the role repository to return the new role when findById is called
                when(roleRepository.findById(newRoleID)).thenReturn(Optional.of(classAdmin));
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(UsersRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .roleName(classAdmin.getRoleName())
                                .build());

                UsersRes updatedUser = usersService.updateUserRoleById(userId, newRoleID);

                assertNotNull(updatedUser);
                assertNotNull(updatedUser.getRoleName());
                assertEquals(classAdmin.getRoleName(), updatedUser.getRoleName());

        }

        @Test
        public void testUpdateUserRoleTrainerById() {
                // Prepare test data
                Users user = Users.builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .role(admin)
                                .build();
                Long userId = user.getId();
                int newRoleID = trainer.getRoleId();
                UsersRes userRes = new UsersRes();

                // Mock the role repository to return the new role when findById is called
                when(roleRepository.findById(newRoleID)).thenReturn(Optional.of(classAdmin));
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(userRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .roleName(trainer.getRoleName())
                                .build());

                UsersRes updatedUser = usersService.updateUserRoleById(userId, newRoleID);

                assertNotNull(updatedUser);
                assertNotNull(updatedUser.getRoleName());
                assertEquals(trainer.getRoleName(), updatedUser.getRoleName());

        }

        @Test
        public void testUpdateUserRoleSuperAdminById() {
                // Prepare test data
                Users user = Users.builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .role(classAdmin)
                                .build();
                Long userId = user.getId();
                int newRoleID = admin.getRoleId();
                UsersRes userRes = new UsersRes();

                // Mock the role repository to return the new role when findById is called
                when(roleRepository.findById(newRoleID)).thenReturn(Optional.of(classAdmin));
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(userRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .roleName(admin.getRoleName())
                                .build());

                UsersRes updatedUser = usersService.updateUserRoleById(userId, newRoleID);

                assertNotNull(updatedUser);
                assertNotNull(updatedUser.getRoleName());
                assertEquals(admin.getRoleName(), updatedUser.getRoleName());

        }

        @Test
        public void testchangeUserStatusFalse() {
                Users user = Users.builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .role(admin)
                                .build();
                Long userId = user.getId();
                UsersRes userRes = new UsersRes();
                when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("you@gmail.com");
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(userRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(false)
                                .roleName(admin.getRoleName())
                                .build());

                UsersRes updatedUser = usersService.changeUserStatus(userId);
                assertNotNull(updatedUser);
                assertFalse(updatedUser.isStatus());
        }

        @Test
        public void testchangeUserStatusTrue() {
                Users user = Users.builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(false)
                                .role(admin)
                                .build();
                Long userId = user.getId();
                UsersRes userRes = new UsersRes();
                when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("you@gmail.com");
                when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
                when(usersRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toRes(any(Users.class))).thenReturn(userRes
                                .builder()
                                .id(2L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .dob(LocalDate.of(2003, 11, 21))
                                .phone("1234567890")
                                .gender("Male")
                                .isFirstLogin(true)
                                .status(true)
                                .roleName(admin.getRoleName())
                                .build());

                UsersRes updatedUser = usersService.changeUserStatus(userId);
                assertNotNull(updatedUser);
                assertTrue(updatedUser.isStatus());
        }

        @Test
        public void testAddNewUser_SendEmail_Success() {
                UsersDTO dto = new UsersDTO();

                // Arrange
                UsersReqCreate usersReqCreate = new UsersReqCreate();
                usersReqCreate.setName("Admin");
                usersReqCreate.setEmail("admin@fpt.com");
                usersReqCreate.setRoleId(1);
                usersReqCreate.setPhone("1234567890");
                usersReqCreate.setDob(LocalDate.of(2003, Calendar.APRIL, 3));
                usersReqCreate.setGender("Male");
                usersReqCreate.setStatus(true);

                Role role = new Role();
                role.setRoleId(1);
                role.setRoleName("ROLE_USER");

                when(roleRepository.findById(role.getRoleId())).thenReturn(Optional.of(role));
                when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(usersConverter.toDTO(any(Users.class))).thenAnswer(invocation -> {
                        Users user = invocation.getArgument(0);
                        dto.setName(user.getName());
                        dto.setEmail(user.getEmail());
                        dto.setPhone(user.getPhone());
                        dto.setDob(user.getDob());
                        dto.setGender(user.getGender());
                        dto.setStatus(user.isStatus());
                        dto.setCreatedBy(user.getCreatedBy() != null ? user.getCreatedBy() : "admin");
                        dto.setCreatedDate(user.getCreatedDate());
                        dto.setFirstLogin(user.isFirstLogin());
                        return dto;
                });
                // Act
                UsersDTO result = usersService.addNewUser(usersReqCreate);

                // Assert
                verify(usersRepository, times(1)).save(any(Users.class));
                verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

                assertNotNull(result);
                assertEquals("Admin", result.getName());
                assertEquals("admin@fpt.com", result.getEmail());
                assertEquals("1234567890", result.getPhone());
                assertEquals("Male", result.getGender());
                assertEquals("admin", result.getCreatedBy());
                assertTrue(result.isFirstLogin());
                assertNotNull(result.getCreatedDate());
        }

        @Test
        public void should_successfully_update_user_information() {
                // Mocking authentication service behavior
                when(authenticationService.getName()).thenReturn("MockUser");

                // Creating test data
                Long id = 1L;
                UsersReqUpdate usersDTO = new UsersReqUpdate();
                usersDTO.setName("John Doe");
                usersDTO.setPhone("123456789");
                usersDTO.setDob(LocalDate.of(2001, Calendar.FEBRUARY, 1));
                usersDTO.setGender("Male");
                usersDTO.setStatus(true);

                // Creating existing user
                Users existingUser = new Users();
                existingUser.setId(id);
                existingUser.setName("Old Name");
                existingUser.setPhone("987654321");
                existingUser.setDob(LocalDate.of(2000, Calendar.FEBRUARY, 2));
                existingUser.setGender("Female");
                existingUser.setStatus(false);

                // Mocking repository behavior
                when(usersRepository.findById(id)).thenReturn(Optional.of(existingUser));
                when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // Mocking converter behavior
                UsersDTO expectedDTO = new UsersDTO(); // Set up expected DTO here
                UsersRes usersRes = new UsersRes();
                when(usersConverter.toDTO(any(Users.class))).thenAnswer(invocationOnMock -> {
                        Users user = invocationOnMock.getArgument(0);
                        UsersDTO usersRes1 = new UsersDTO();
                        usersRes1.setName(user.getName());
                        usersRes1.setPhone(user.getPhone());
                        usersRes1.setDob(user.getDob());
                        usersRes1.setGender(user.getGender());
                        usersRes1.setStatus(user.isStatus());
                        usersRes1.setModifiedBy(user.getModifiedBy());
                        return usersRes1;
                });
                // Calling the method under test
                UsersDTO result = usersService.updateUserInformation(id, usersDTO);

                // Assertions

                assertEquals(usersDTO.getName(), result.getName());
                assertEquals(usersDTO.getPhone(), result.getPhone());
                assertEquals(usersDTO.getDob(), result.getDob());
                assertEquals(usersDTO.getGender(), result.getGender());
                assertEquals(usersDTO.isStatus(), result.isStatus());
                assertEquals("MockUser", result.getModifiedBy());
        }

        @Test
        public void should_successfully_throws_exception_when_user_not_found() {
                // Creating test data
                Long id = 1L;
                UsersReqUpdate usersDTO = new UsersReqUpdate();
                usersDTO.setName("John Doe");
                usersDTO.setPhone("123456789");
                usersDTO.setDob(LocalDate.of(2001, Calendar.FEBRUARY, 1));
                usersDTO.setGender("Male");
                usersDTO.setStatus(true);

                // Mocking repository behavior for not found ID
                when(usersRepository.findById(id)).thenReturn(Optional.empty());

                // Calling the method under test and asserting NotFoundException
                assertThrows(RuntimeException.class, () -> usersService.updateUserInformation(id, usersDTO));
        }

        @Test
        public void testGetUserById() {

                Long userId = 1L;
                Users users = new Users();
                users.setName("Admin");
                users.setEmail("admin@fpt.com");
                users.setDob(LocalDate.of(2023, Calendar.APRIL, 3));
                users.setPhone("010010");
                users.setGender("male");
                Role role = new Role();
                role.setRoleId(1);
                role.setRoleName("SUPPER_ADMIN");
                users.setRole(role);
                users.setStatus(true);
                users.setFirstLogin(true);

                when(usersRepository.findById(userId))
                                .thenReturn(Optional.of(users));
                when(usersConverter.toRes(users))
                                .thenReturn(new UsersRes(
                                                1L,
                                                "Admin",
                                                "admin@fpt.com",
                                                LocalDate.of(2023, Calendar.APRIL, 3),
                                                "010010",
                                                "male",
                                                "SUPPER_ADMIN",
                                                true,
                                                true));

                UsersRes usersRes = usersService.getUserById(userId);

                assertEquals(usersRes.getName(), users.getName());
                assertEquals(usersRes.getEmail(), users.getEmail());
                assertEquals(usersRes.getDob(), users.getDob());
                assertEquals(usersRes.getPhone(), users.getPhone());
                assertEquals(usersRes.getGender(), users.getGender());
                assertEquals(usersRes.getRoleName(), users.getRole().getRoleName());
                assertEquals(usersRes.isFirstLogin(), users.isFirstLogin());
                assertEquals(usersRes.isStatus(), users.isStatus());

                verify(usersRepository, times(1)).findById(userId);

        }

//        @Test
//        public void testSearchUsersBySearchKey() {
//                String searchKey = "ad";
//                int page = 0;
//                int size = 1;
//                Pageable pageable = PageRequest.of(page, size);
//                List<Users> usersList = new ArrayList<>();
//                Users users = new Users();
//                users.setName("Admin");
//                users.setEmail("admin@fpt.com");
//                users.setDob(LocalDate.of(2023, Calendar.APRIL, 3));
//                users.setPhone("010010");
//                users.setGender("male");
//                Role role = new Role();
//                role.setRoleId(1);
//                role.setRoleName("SUPPER_ADMIN");
//                users.setRole(role);
//                users.setStatus(true);
//                users.setFirstLogin(true);
//                usersList.add(users);
//                Page<Users> usersPage = new PageImpl<Users>(usersList);
//
//                when(usersRepository.searchAcrossFields(searchKey, pageable))
//                                .thenReturn(usersPage);
//                when(usersConverter.toRes(any(Users.class)))
//                                .thenReturn(new UsersRes(
//                                                1L,
//                                                "Admin",
//                                                "admin@fpt.com",
//                                                LocalDate.of(2023, Calendar.APRIL, 3),
//                                                "010010",
//                                                "male",
//                                                "SUPPER_ADMIN",
//                                                true,
//                                                true));
//
//                Page<UsersRes> usersRes = usersService.searchUsersBySearchKey(searchKey, page, size);
//
//                assertEquals(usersPage.getSize(), usersRes.getSize());
//
//                verify(usersRepository, times(1))
//                                .searchAcrossFields(searchKey, pageable);
//
//        }

        @Test
        public void testGetAllUserPage() {
                // Mock data
                Role role = new Role();
                role.setRoleId(1);
                role.setRoleName("ROLE_USER");

                Users user1 = Users.builder()
                                .id(1L)
                                .name("user1")
                                .email("user1@example.com")
                                .password("password1")
                                .role(role)
                                .build();

                UsersRes userRes = new UsersRes();
                userRes.setId(user1.getId());
                userRes.setName(user1.getName());
                userRes.setEmail(user1.getEmail());
                userRes.setRoleName(role.getRoleName());

                UsersDTO userDTO = new UsersDTO();
                userDTO.setId(user1.getId());
                userDTO.setName(user1.getName());
                userDTO.setEmail(user1.getEmail());
                userDTO.setRoleName(role.getRoleName());
                // Set other attributes as needed

                // Mocking repository
                List<Users> userList = Collections.singletonList(user1);
                Pageable pageable = PageRequest.of(0, 10);
                Page<Users> page = new PageImpl<>(userList, pageable, userList.size());
                when(usersRepository.findAll(pageable)).thenReturn(page);

                // Mocking converter
                when(usersConverter.toRes(user1)).thenReturn(userRes);

                // Perform service call
                Page<UsersRes> resultPage = usersService.getAllUserPage(0, 10);

                // Assertions
                assertEquals(1, resultPage.getTotalElements());
                assertEquals(user1.getId(), resultPage.getContent().get(0).getId());
                assertEquals(user1.getName(), resultPage.getContent().get(0).getName());
                assertEquals(role.getRoleName(), resultPage.getContent().get(0).getRoleName());
        }

        @Test
        public void shouldSuccessfullyGetUserById() {

                Long userId = 1L;
                Users users = new Users();
                users.setName("Admin");
                users.setEmail("admin@fpt.com");
                users.setDob(LocalDate.of(2023, Calendar.APRIL, 3));
                users.setPhone("010010");
                users.setGender("male");
                Role role = new Role();
                role.setRoleId(1);
                role.setRoleName("SUPPER_ADMIN");
                users.setRole(role);
                users.setStatus(true);
                users.setFirstLogin(true);

                when(usersRepository.findById(userId))
                                .thenReturn(Optional.of(users));
                when(usersConverter.toRes(users))
                                .thenReturn(new UsersRes(
                                                1L,
                                                "Admin",
                                                "admin@fpt.com",
                                                LocalDate.of(2023, Calendar.APRIL, 3),
                                                "010010",
                                                "male",
                                                "SUPPER_ADMIN",
                                                true,
                                                true));

                UsersRes usersRes = usersService.getUserById(userId);

                assertNotNull(usersRes);
                assertEquals(usersRes.getName(), users.getName());
                assertEquals(usersRes.getEmail(), users.getEmail());
                assertEquals(usersRes.getDob(), users.getDob());
                assertEquals(usersRes.getPhone(), users.getPhone());
                assertEquals(usersRes.getGender(), users.getGender());
                assertEquals(usersRes.getRoleName(), users.getRole().getRoleName());
                assertEquals(usersRes.isFirstLogin(), users.isFirstLogin());
                assertEquals(usersRes.isStatus(), users.isStatus());

                verify(usersRepository, times(1)).findById(userId);

        }

        @Test
        public void shouldThrowApiExceptionWhenUserIdNotFound() {
                Long userId = 1L;
                Users users = new Users();
                users.setName("Admin");
                users.setEmail("admin@fpt.com");
                users.setDob(LocalDate.of(2023, Calendar.APRIL, 3));
                users.setPhone("010010");
                users.setGender("male");
                Role role = new Role();
                role.setRoleId(1);
                role.setRoleName("SUPPER_ADMIN");
                users.setRole(role);
                users.setStatus(true);
                users.setFirstLogin(true);

                when(usersRepository.findById(userId))
                                .thenReturn(Optional.empty());

                ApiException exception = assertThrows(ApiException.class, () -> {
                        usersService.getUserById(userId);
                });

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
                assertEquals("User not found with id: " + userId, exception.getMessage());

        }

        @Test
        public void testGetClassAdminList_withName() {
                // Setup
                String name = "Admin";
                Users user1 = new Users();
                user1.setId(1L);
                user1.setName("Admin");
                UserAvailable userAvailable1 = new UserAvailable();
                userAvailable1.setId(1L);
                userAvailable1.setName("Admin");

                when(usersRepository.findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(2, true, name))
                        .thenReturn(List.of(user1));
                when(usersConverter.toAvailableUser(user1)).thenReturn(userAvailable1);

                // Execute
                List<UserAvailable> result = usersService.getClassAdminList("Admin");

                // Verify
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(1, result.size());
                // You can add more detailed assertions here

                // Verify interactions
                verify(usersRepository).findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(2, true, name);
                verify(usersConverter).toAvailableUser(user1);
        }

        @Test
        public void testGetClassAdminList_withNullName() {
                // Setup

                Users user1 = new Users();
                user1.setId(1L);
                user1.setName("Admin");
                UserAvailable userAvailable1 = new UserAvailable();
                userAvailable1.setId(1L);
                userAvailable1.setName("Admin");

                when(usersRepository.findAllByRole_RoleIdAndStatus(2, true))
                        .thenReturn(List.of(user1));
                when(usersConverter.toAvailableUser(user1)).thenReturn(userAvailable1);

                // Execute
                List<UserAvailable> result = usersService.getClassAdminList(null);

                // Verify
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(1, result.size());
                // You can add more detailed assertions here

                // Verify interactions
                verify(usersRepository).findAllByRole_RoleIdAndStatus(2, true);
                verify(usersConverter).toAvailableUser(user1);
        }

        @Test
        public void testGetClassTrainerList_withName() {
                // Setup
                String name = "Trainer";
                Users user1 = new Users();
                user1.setId(1L);
                user1.setName("Trainer");
                UserAvailable userAvailable1 = new UserAvailable();
                userAvailable1.setId(1L);
                userAvailable1.setName("Trainer");

                when(usersRepository.findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(3, true, name))
                        .thenReturn(List.of(user1));
                when(usersConverter.toAvailableUser(user1)).thenReturn(userAvailable1);

                // Execute
                List<UserAvailable> result = usersService.getTrainerList("Trainer");

                // Verify
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(1, result.size());
                // You can add more detailed assertions here

                // Verify interactions
                verify(usersRepository).findAllByRole_RoleIdAndStatusAndNameContainingIgnoreCase(3, true, name);
                verify(usersConverter).toAvailableUser(user1);
        }

        @Test
        public void testGetClassTrainerList_withNullName() {
                // Setup

                Users user1 = new Users();
                user1.setId(1L);
                user1.setName("Trainer");
                UserAvailable userAvailable1 = new UserAvailable();
                userAvailable1.setId(1L);
                userAvailable1.setName("Trainer");

                when(usersRepository.findAllByRole_RoleIdAndStatus(3, true))
                        .thenReturn(List.of(user1));
                when(usersConverter.toAvailableUser(user1)).thenReturn(userAvailable1);

                // Execute
                List<UserAvailable> result = usersService.getTrainerList(null);

                // Verify
                assertNotNull(result);
                assertFalse(result.isEmpty());
                assertEquals(1, result.size());
                // You can add more detailed assertions here

                // Verify interactions
                verify(usersRepository).findAllByRole_RoleIdAndStatus(3, true);
                verify(usersConverter).toAvailableUser(user1);
        }

        @Test
        void searchUsersWithBothDatesProvided() {
                // Setup
                String searchKey = "test";
                LocalDate startDate = LocalDate.now().minusDays(10);
                LocalDate endDate = LocalDate.now();
                List<String> gender = Arrays.asList("Male", "Female");
                List<String> roles = Arrays.asList("Admin", "User");
                Page<Users> usersPage = new PageImpl<>(Arrays.asList(new Users()));
                when(usersRepository.searchByDateNotNull(eq(searchKey), eq(startDate), eq(endDate), eq(gender), eq(roles), any(Pageable.class)))
                        .thenReturn(usersPage);
                when(usersConverter.toRes(any(Users.class))).thenAnswer(invocation -> new UsersRes());

                // Execute
                Page<UsersRes> result = usersService.searchUsersBySearchKey(searchKey, startDate, endDate, gender, roles, 0, 5);

                // Verify
                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                verify(usersRepository).searchByDateNotNull(eq(searchKey), eq(startDate), eq(endDate), eq(gender), eq(roles), any(Pageable.class));
                verify(usersConverter, atLeastOnce()).toRes(any(Users.class));
        }

        @Test
        void searchUsersWithStartDateOnlyThrowsException() {
                LocalDate startDate = LocalDate.now().minusDays(10);

                ApiException exception = assertThrows(ApiException.class, () ->
                        usersService.searchUsersBySearchKey("test", startDate, null, Arrays.asList("Male"), Arrays.asList("Admin"), 0, 5));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
                assertEquals("End Date must not be null or empty", exception.getMessage());
        }

        @Test
        void searchUsersWithEndDateOnlyThrowsException() {
                LocalDate endDate = LocalDate.now();

                ApiException exception = assertThrows(ApiException.class, () ->
                        usersService.searchUsersBySearchKey("test", null, endDate, Arrays.asList("Male"), Arrays.asList("Admin"), 0, 5));

                assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
                assertEquals("Start Date must not be null or empty", exception.getMessage());
        }

        @Test
        void searchUsersWithoutDates() {
                // Similar setup to the first test but without startDate and endDate
                String searchKey = "test";
                List<String> gender = Arrays.asList("Male", "Female");
                List<String> roles = Arrays.asList("Admin", "User");
                Page<Users> usersPage = new PageImpl<>(Arrays.asList(new Users()));
                when(usersRepository.searchByDateNull(eq(searchKey), eq(gender), eq(roles), any(Pageable.class)))
                        .thenReturn(usersPage);
                when(usersConverter.toRes(any(Users.class))).thenAnswer(invocation -> new UsersRes());

                // Execute
                Page<UsersRes> result = usersService.searchUsersBySearchKey(searchKey, null, null, gender, roles, 0, 5);

                // Verify
                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                verify(usersRepository).searchByDateNull(eq(searchKey), eq(gender), eq(roles), any(Pageable.class));
                verify(usersConverter, atLeastOnce()).toRes(any(Users.class));
        }

}
