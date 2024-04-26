package com.example.phase1_fams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyllabusOutlineRes {
    private List<DaysUnitRes> days = new ArrayList<>();

    public void addDayUnit(DaysUnitRes daysUnit) {
        if (daysUnit != null) days.add(daysUnit);
    }
}
