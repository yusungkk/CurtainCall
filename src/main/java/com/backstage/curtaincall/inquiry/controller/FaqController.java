package com.backstage.curtaincall.inquiry.controller;

import com.backstage.curtaincall.inquiry.dto.request.CreateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.request.UpdateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.response.FaqResponse;
import com.backstage.curtaincall.inquiry.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/faqs")
    public ResponseEntity<Page<FaqResponse>> getFaqs(
            @RequestParam(required = false, name = "faq-type") String faqType,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        Page<FaqResponse> findFaqs = null;
        if (StringUtils.hasText(faqType)) {
            findFaqs = faqService.findAllByType(faqType, offset, limit);
        } else {
            findFaqs = faqService.findAll(offset, limit);
        }

        return ResponseEntity
                .status(OK)
                .body(findFaqs);
    }

    @GetMapping("/faqs/{id}")
    public ResponseEntity<FaqResponse> getFaq(@PathVariable Long id) {
        return ResponseEntity.ok(faqService.findFaq(id));
    }


    @ResponseStatus(CREATED)
    @PostMapping("/admin/faqs/new")
    public void createFaq(@RequestBody CreateFaqRequest request) {
        faqService.createFaq(request);
    }

    @ResponseStatus(NO_CONTENT)
    @PatchMapping("/admin/faqs/{id}")
    public void updateFaq(@RequestBody UpdateFaqRequest request) {
        faqService.updateFaq(request);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/admin/faqs/{id}")
    public void deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
    }
}
