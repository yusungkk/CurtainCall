package com.backstage.curtaincall.inquiry.controller;

import com.backstage.curtaincall.inquiry.dto.request.CreateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.request.UpdateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.response.FaqResponse;
import com.backstage.curtaincall.inquiry.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/faqs")
    public ResponseEntity<Page<FaqResponse>> getFaqs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity
                .status(OK)
                .body(faqService.findAll(offset, limit));
    }

    @GetMapping("/faqs/faq-type")
    public ResponseEntity<Page<FaqResponse>> getFaqsByType(
            String faqType,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity
                .status(OK)
                .body(faqService.findAllByType(faqType, offset, limit));
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

    @DeleteMapping("/admin/faqs/{id}")
    public void deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
    }
}
