package com.example.phase1_fams.service;

import com.example.phase1_fams.dto.RolePermissionDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.model.*;
import com.example.phase1_fams.repository.RoleRepository;
import com.example.phase1_fams.service.impl.RolePermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RolePermissionServiceTests {

    @InjectMocks
    private RolePermissionServiceImpl rolePermissionService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ModelMapper modelMapper;

    Role admin;
    Role classAdmin;
    Role trainer;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void init(){
        admin = Role.builder()
                .roleId(1)
                .roleName("SUPPER_ADMIN")
                .classPermissionGroup(ClassPermissionGroup.FULL_ACCESS)
                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.FULL_ACCESS)
                .syllabusPermissionGroup(SyllabusPermissionGroup.FULL_ACCESS)
                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.FULL_ACCESS)
                .userPermissionGroup(UserPermissionGroup.FULL_ACCESS)
                .build();
        classAdmin = Role.builder()
                .roleId(2)
                .roleName("CLASS_ADMIN")
                .classPermissionGroup(ClassPermissionGroup.VIEW)
                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.MODIFY)
                .syllabusPermissionGroup(SyllabusPermissionGroup.MODIFY)
                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.MODIFY)
                .userPermissionGroup(UserPermissionGroup.CREATE)
                .build();
        trainer = Role.builder()
                .roleId(3)
                .roleName("TRAINER")
                .classPermissionGroup(ClassPermissionGroup.VIEW)
                .learningMaterialPermissionGroup(LearningMaterialPermissionGroup.VIEW)
                .syllabusPermissionGroup(SyllabusPermissionGroup.VIEW)
                .trainingProgramPermissionGroup(TrainingProgramPermissionGroup.VIEW)
                .userPermissionGroup(UserPermissionGroup.NO_ACCESS)
                .build();
    }

    @Test
    public void testGetPermissionForEachRole_Success(){
        RolePermissionDTO adminDTO = new RolePermissionDTO(
                "SUPPER_ADMIN",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS");
        RolePermissionDTO classAdminDTO = new RolePermissionDTO(
                "SUPPER_ADMIN",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS");
        RolePermissionDTO trainerDTO = new RolePermissionDTO(
                "SUPPER_ADMIN",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS",
                "FULL_ACCESS");

        List<Role> roles = Arrays.asList(admin, classAdmin, trainer);

        when(roleRepository.findAll()).thenReturn(roles);

        when(modelMapper.map(admin, RolePermissionDTO.class)).thenReturn(adminDTO);
        when(modelMapper.map(classAdmin, RolePermissionDTO.class)).thenReturn(classAdminDTO);
        when(modelMapper.map(trainer, RolePermissionDTO.class)).thenReturn(trainerDTO);

        //When
        List<RolePermissionDTO> rolePermissionDTOS = rolePermissionService.getPermissionForRoles();

        //Then
        assertNotNull(rolePermissionDTOS);
        assertEquals(roles.size(), rolePermissionDTOS.size());

        verify(roleRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(admin, RolePermissionDTO.class);
        verify(modelMapper, times(1)).map(classAdmin, RolePermissionDTO.class);
        verify(modelMapper, times(1)).map(trainer, RolePermissionDTO.class);
    }

    @Test
    public void testUpdatePermission_Success() {
        //Setup test data
        RolePermissionDTO adminDto = new RolePermissionDTO("SUPPER_ADMIN", "VIEW", "FULL_ACCESS", "FULL_ACCESS", "FULL_ACCESS");
        RolePermissionDTO classAdminDto = new RolePermissionDTO("CLASS_ADMIN", "VIEW", "VIEW", "NO_ACCESS", "FULL_ACCESS");
        List<RolePermissionDTO> permissionsDTO = Arrays.asList(adminDto, classAdminDto);

//        List<RolePermissionDTO> expectedListDTO = new ArrayList<>();
        when(roleRepository.findByRoleName(adminDto.getRoleName())).thenReturn(Optional.of(admin));
        when(roleRepository.findByRoleName(classAdminDto.getRoleName())).thenReturn(Optional.of(classAdmin));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Role.class), eq(RolePermissionDTO.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            RolePermissionDTO dto = new RolePermissionDTO();
            dto.setRoleName(role.getRoleName());
            dto.setClassPermission(String.valueOf(role.getClassPermissionGroup()));
            dto.setSyllabusPermission(String.valueOf(role.getSyllabusPermissionGroup()));
            dto.setLearningMaterialPermission(String.valueOf(role.getLearningMaterialPermissionGroup()));
            dto.setTrainingProgramPermission(String.valueOf(role.getTrainingProgramPermissionGroup()));
            return dto;
        });
//        // Assume getPermissionForRoles() is properly mocked to return an expected result
//        when(rolePermissionService.getPermissionForRoles()).thenReturn(permissionsDTO);

        // Execute the method under test
        List<RolePermissionDTO> updatedPermissions = rolePermissionService.updatePermissionsForRoles(permissionsDTO);
        RolePermissionDTO updatedAdminDTO = updatedPermissions.get(0);
        RolePermissionDTO updatedClassAdminDTO = updatedPermissions.get(1);
        // Verify the result
        assertNotNull(updatedAdminDTO);
        assertNotNull(updatedClassAdminDTO);
        assertEquals(2, updatedPermissions.size());

        assertEquals(adminDto.getRoleName(), updatedAdminDTO.getRoleName());
        assertEquals(adminDto.getClassPermission(), updatedAdminDTO.getClassPermission());
        assertEquals(adminDto.getSyllabusPermission(), updatedAdminDTO.getSyllabusPermission());
        assertEquals(adminDto.getLearningMaterialPermission(), updatedAdminDTO.getLearningMaterialPermission());
        assertEquals(adminDto.getTrainingProgramPermission(), updatedAdminDTO.getTrainingProgramPermission());

        assertEquals(classAdminDto.getRoleName(), updatedClassAdminDTO.getRoleName());
        assertEquals(classAdminDto.getClassPermission(), updatedClassAdminDTO.getClassPermission());
        assertEquals(classAdminDto.getSyllabusPermission(), updatedClassAdminDTO.getSyllabusPermission());
        assertEquals(classAdminDto.getLearningMaterialPermission(), updatedClassAdminDTO.getLearningMaterialPermission());
        assertEquals(classAdminDto.getTrainingProgramPermission(), updatedClassAdminDTO.getTrainingProgramPermission());


        // Verify interactions
        verify(roleRepository, times(2)).findByRoleName(anyString());
        verify(roleRepository, times(2)).save(any(Role.class));
    }

    @Test
    public void testUpdatePermission_RoleNotFound() {
        // Setup DTO with a role name that doesn't exist
        RolePermissionDTO unknownRoleDto = new RolePermissionDTO("SUPPER_ADMIN", "VIE", "VIEW", "VIEW", "VIEW");
        List<RolePermissionDTO> permissionsDTO = Arrays.asList(unknownRoleDto);

        when(roleRepository.findByRoleName("SUPPER_ADMIN")).thenReturn(Optional.ofNullable(admin));

        // Execute and assert exception
        ApiException exception = assertThrows(ApiException.class, () -> {
            rolePermissionService.updatePermissionsForRoles(permissionsDTO);
        });

        // Verify exception message or status
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Wrong permission name or type", exception.getMessage());
    }

    @Test
    public void testUpdatePermission_WrongPermissionName() {
        // Setup DTO with a role name that doesn't exist
        RolePermissionDTO unknownRoleDto = new RolePermissionDTO("UNKNOWN_ROLE", "VIEW", "VIEW", "VIEW", "VIEW");
        List<RolePermissionDTO> permissionsDTO = Arrays.asList(unknownRoleDto);

        when(roleRepository.findByRoleName("UNKNOWN_ROLE")).thenReturn(Optional.empty());

        // Execute and assert exception
        ApiException exception = assertThrows(ApiException.class, () -> {
            rolePermissionService.updatePermissionsForRoles(permissionsDTO);
        });

        // Verify exception message or status
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Role not found", exception.getMessage());
    }
}