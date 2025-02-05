package com.backstage.curtaincall.inquiry.dto.request;

import lombok.Data;

@Data
public class UpdateFaqRequest {
    private Long id;
    private String answer;
}
