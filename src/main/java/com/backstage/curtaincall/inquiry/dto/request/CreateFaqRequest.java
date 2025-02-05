package com.backstage.curtaincall.inquiry.dto.request;

import com.backstage.curtaincall.inquiry.entity.FaqType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFaqRequest {

    private String question;
    private String answer;
    private FaqType type;
}
