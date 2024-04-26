package com.example.phase1_fams.converter;


import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.exception.ApiException;
import com.example.phase1_fams.dto.response.UserAvailable;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.model.Role;
import com.example.phase1_fams.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UsersConverterTests {
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsersConverter usersConverter;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToRes() {

        // Prepare test data
        Role role = new Role();
        role.setRoleName("ROLE_USER");

        Users user = new Users();
        user.setId(1L);
        user.setName("testuser");
        user.setRole(role);

        UsersRes expectedRes = new UsersRes();
        expectedRes.setId(1L);
        expectedRes.setName("testuser");
        expectedRes.setRoleName("ROLE_USER");

        // Mock behavior of ModelMapper
        when(modelMapper.map(user, UsersRes.class)).thenReturn(expectedRes);

        // Test the method
        UsersRes actualRes = usersConverter.toRes(user);

        // Verify the result
        assertEquals(expectedRes, actualRes);
    }

    @Test
    public void testToDTO() {

        // Prepare test data
        Role role = new Role();
        role.setRoleName("ROLE_USER");

        Users user = new Users();
        user.setId(1L);
        user.setName("testuser");
        user.setRole(role);

        UsersDTO expectedDTO = new UsersDTO();
        expectedDTO.setId(1L);
        expectedDTO.setName("testuser");

        // Mock behavior of ModelMapper
        when(modelMapper.map(user, UsersDTO.class)).thenReturn(expectedDTO);

        // Test the method
        UsersDTO actualDTO = usersConverter.toDTO(user);

        // Verify the result
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    public void testToAvailableUser() {
        // Mock or create a real Users entity
        Users user = new Users();
        user.setId(1L);
        user.setName("John Doe");
        // Assume Role is another entity related to Users
        Role role = new Role();
        role.setRoleName("Admin");
        user.setRole(role); // Set the role for the user

        when(this.modelMapper.map(any(Users.class), eq(UserAvailable.class))).thenReturn(new UserAvailable(1L, "John Doe", null));
        // Execute the method under test
        UserAvailable result = usersConverter.toAvailableUser(user);

        // Assertions to verify the outcome
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getRole().getRoleName(), result.getRoleName());
    }
}