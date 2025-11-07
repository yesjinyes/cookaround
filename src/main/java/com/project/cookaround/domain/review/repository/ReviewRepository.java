package com.project.cookaround.domain.review.repository;

import com.project.cookaround.domain.review.entity.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    public Long countByMemberId(Long memberId) {
        return em.createQuery("select count(r) from Review r where r.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    // 사용자가 작성한 레시피에 달린 후기들의 평균
    public Double findAverageRatingByMemberId(Long memberId) {
        return em.createQuery("select avg(rv.rating) from Review rv where rv.recipe.id in (" +
                        "select id from Recipe rc where rc.member.id = :memberId)", Double.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    // 마이페이지 내가 쓴 글/후기 - 후기 조회
    public List<Review> findByMemberIdOrderById(Long memberId) {
        return em.createQuery("select r from Review r where r.member.id = :memberId order by r.id desc", Review.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
