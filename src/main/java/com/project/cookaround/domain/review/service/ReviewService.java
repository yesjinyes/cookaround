package com.project.cookaround.domain.review.service;

import com.project.cookaround.domain.review.entity.Review;
import com.project.cookaround.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // 마이페이지 - 내가 쓴 글/후기, 리뷰 개수 출력
    public Long getReviewCountByMemberId(Long memberId) {
        Long cnt = reviewRepository.countByMemberId(memberId);
        if (cnt != 0) {
            return cnt;
        }
        return 0L;
    }

    // 마이페이지 - 내가 쓴 글/후기, 사용자가 작성한 레시피에 달린 후기들의 평균
    public Double getAverageRatingByMemberId(Long memberId) {
        Double avg = reviewRepository.findAverageRatingByMemberId(memberId);
        if (avg != null) {
            return avg;
        }
        return 0.0;
    }

    // 마이페이지 - 내가 쓴 글/후기 - 후기 조회
    public List<Review> getReviewByMemberId(Long memberId) {
        return reviewRepository.findByMemberIdOrderById(memberId);
    }

}
