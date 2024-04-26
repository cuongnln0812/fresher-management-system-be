package com.example.phase1_fams.converter;


import com.example.phase1_fams.dto.response.UserAvailable;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

import com.example.phase1_fams.dto.UsersDTO;
import com.example.phase1_fams.dto.response.UsersRes;
import com.example.phase1_fams.model.Users;

@Component
public class UsersConverter {

    private final ModelMapper modelMapper;

    public UsersConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        // Create a PropertyMap to specify the mapping configuration
        PropertyMap<Users, UsersDTO> userMap = new PropertyMap<>() {
            protected void configure() {
                using(ctx -> ((Users) ctx.getSource()).getRole().getRoleName())
                        .map(source, destination.getRoleName());
            }
        };
        PropertyMap<Users, UsersRes> userResMap = new PropertyMap<Users, UsersRes>() {
            protected void configure() {
                using(ctx -> ((Users) ctx.getSource()).getRole().getRoleName())
                        .map(source, destination.getRoleName());
            }
        };
        // Add the PropertyMap to the modelMapper configuration
        modelMapper.addMappings(userMap);
        modelMapper.addMappings(userResMap);

    }

    public UsersRes toRes(Users entity) {
        UsersRes res = this.modelMapper.map(entity, UsersRes.class);
        if (entity.getRole() != null) {
            res.setRoleName(entity.getRole().getRoleName());
        }
        return res;
    }

    public UserAvailable toAvailableUser(Users entity) {
        UserAvailable res = this.modelMapper.map(entity, UserAvailable.class);
        if (entity.getRole() != null) {
            res.setRoleName(entity.getRole().getRoleName());
        }
        return res;
    }

    public UsersDTO toDTO(Users entity) {
        return modelMapper.map(entity, UsersDTO.class);
    }
}
