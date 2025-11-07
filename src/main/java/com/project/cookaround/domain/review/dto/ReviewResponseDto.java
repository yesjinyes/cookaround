package com.project.cookaround.domain.review.dto;

import com.project.cookaround.domain.review.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter @Setter
public class ReviewResponseDto {

    private Long id;
    private String category; // 후기를 작성한 요리팁의 카테고리
    private String content; // 후기 내용
    private String createdAt; // 작성일자

    // Entity -> Dto 변환
    public static ReviewResponseDto fromEntity(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setCategory(review.getRecipe().getCategory().getDescription());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return dto;
    }

}
