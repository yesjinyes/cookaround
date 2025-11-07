package com.project.cookaround.domain.review.controller;

import com.project.cookaround.common.security.CustomUserDetails;
import com.project.cookaround.domain.review.dto.ReviewResponseDto;
import com.project.cookaround.domain.review.entity.Review;
import com.project.cookaround.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ResponseBody
    @GetMapping("/api/members/mypage/reviews")
    public List<ReviewResponseDto> listByMemberId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReviewResponseDto> reviews = new ArrayList<>();
        for (Review review : reviewService.getReviewByMemberId(userDetails.getId())) {
            reviews.add(ReviewResponseDto.fromEntity(review));
        }

        return reviews;
    }
}
