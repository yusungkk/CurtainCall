package com.backstage.curtaincall.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqResponse {

    private String answer;
    private String quastion;
}
