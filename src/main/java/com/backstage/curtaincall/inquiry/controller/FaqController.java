package com.backstage.curtaincall.inquiry.controller;

import com.backstage.curtaincall.inquiry.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
}
