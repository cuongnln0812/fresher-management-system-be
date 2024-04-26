package com.example.phase1_fams.converter;

import com.example.phase1_fams.dto.LearningMaterialDto;
import com.example.phase1_fams.model.LearningMaterial;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LearningMaterialConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public LearningMaterialConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public LearningMaterialDto convertToDto(LearningMaterial material) {
        return modelMapper.map(material, LearningMaterialDto.class);
    }

    public LearningMaterial convertToEntity(LearningMaterialDto materialDto) {
        return modelMapper.map(materialDto, LearningMaterial.class);
    }
}
