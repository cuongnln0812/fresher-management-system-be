package com.example.phase1_fams.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@JsonSerialize
public class MaterialRes {
    List<String> uploadedFile = new ArrayList<>();
    List<Map<String, String>> deletedFile = new ArrayList<>();
}
