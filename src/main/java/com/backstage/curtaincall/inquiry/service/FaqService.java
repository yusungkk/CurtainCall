package com.backstage.curtaincall.inquiry.service;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.inquiry.dto.request.CreateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.request.UpdateFaqRequest;
import com.backstage.curtaincall.inquiry.dto.response.FaqResponse;
import com.backstage.curtaincall.inquiry.entity.Faq;
import com.backstage.curtaincall.inquiry.entity.QuestionType;
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

    @Transactional(readOnly = true)
    public Page<FaqResponse> findAll(int offset, int limit) {
        return faqRepository.findAll(PageRequest.of(offset, limit))
                .map(faq -> new FaqResponse(faq.getId(), faq.getType(), faq.getAnswer(), faq.getQuestion()));
    }

    @Transactional(readOnly = true)
    public FaqResponse findFaq(Long id) {

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new CustomException(FAQ_NOT_FOUND));

        return new FaqResponse(faq.getId(), faq.getType(), faq.getAnswer(), faq.getQuestion());
    }

    @Transactional(readOnly = true)
    public Page<FaqResponse> findAllByType(String type, int offset, int limit) {

        QuestionType questionType = getQuestionType(type);
        PageRequest pageRequest = PageRequest.of(offset, limit);

        return faqRepository.findAllByFaqType(questionType, pageRequest)
                .map(faq -> new FaqResponse(faq.getId(), faq.getType(), faq.getAnswer(), faq.getQuestion()));
    }

    @Transactional
    public void createFaq(CreateFaqRequest request) {
        Faq faq = Faq.create(request.getAnswer(), request.getQuestion(), request.getType());
        faqRepository.save(faq);
    }

    @Transactional
    public void updateFaq(UpdateFaqRequest request) {

        Faq faq = faqRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(FAQ_NOT_FOUND));

        faq.updateFaq(request.getAnswer(), request.getQuestion(), request.getType());
    }

    @Transactional
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    private QuestionType getQuestionType(String type) {

        QuestionType questionType;

        try {
            questionType = QuestionType.valueOf(type);
        } catch (Exception e) {
            throw new CustomException(FAQ_TYPE_NOT_FOUND);
        }

        return questionType;
    }
}
