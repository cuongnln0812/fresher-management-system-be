package com.example.phase1_fams.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassUserReq {
    Long userId;

    String userType;
}
