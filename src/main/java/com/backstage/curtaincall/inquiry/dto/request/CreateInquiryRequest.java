package com.backstage.curtaincall.inquiry.dto.request;

import lombok.Data;

@Data
public class CreateInquiryRequest {
    private String questionType;
    private String title;
    private String content;
}
