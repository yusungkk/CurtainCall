package com.backstage.curtaincall.inquiry.dto.request;

import com.backstage.curtaincall.inquiry.entity.FaqType;
import lombok.Data;

@Data
public class UpdateFaqRequest {
    private Long id;
    private FaqType type;
    private String question;
    private String answer;
}
