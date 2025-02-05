package com.backstage.curtaincall.inquiry.service;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.inquiry.dto.request.CreateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.request.UpdateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.response.FaqResponse;
import com.backstage.curtaincall.inquiry.entity.Faq;
import com.backstage.curtaincall.inquiry.entity.FaqType;
import com.backstage.curtaincall.inquiry.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.FAQ_NOT_FOUND;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.FAQ_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional
    public void createFaq(CreateFaqRequest request) {
        Faq faq = Faq.create(request.getAnswer(), request.getQuestion(), request.getType());
        faqRepository.save(faq);
    }

    @Transactional(readOnly = true)
    public Page<FaqResponse> findAllByType(String type, int offset, int limit) {

        FaqType faqType = getFaqType(type);
        PageRequest pageRequest = PageRequest.of(offset, limit);

        return faqRepository.findAllByFaqType(faqType, pageRequest)
                .map(faq -> new FaqResponse(faq.getAnswer(), faq.getQuestion()));
    }

    @Transactional
    public void updateAnswer(UpdateFaqRequest request) {

        Faq faq = faqRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(FAQ_NOT_FOUND));

        faq.updateAnswer(request.getAnswer());
    }

    private FaqType getFaqType(String type) {

        FaqType faqType;

        try {
            faqType = FaqType.valueOf(type);
        } catch (Exception e) {
            throw new CustomException(FAQ_TYPE_NOT_FOUND);
        }

        return faqType;
    }
}
