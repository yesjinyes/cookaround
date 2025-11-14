package com.project.cookaround.domain.recipe.repository;

import com.project.cookaround.domain.recipe.entity.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecipeRepository {

    @PersistenceContext
    private EntityManager em;

    // 마이페이지 - 내가 쓴 글/후기 - 레시피 개수 조회
    public Long countByMemberId(Long memberId) {
        return em.createQuery("select count(r) from Recipe r where r.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    // 마이페이지 - 내가 쓴 글/후기 - 레시피 조회
    public List<Recipe> findByMemberIdOrderByIdDesc(Long memberId, int page, int PAGE_SIZE) {
        return em.createQuery("select r from Recipe r where r.member.id = :memberId order by r.id desc", Recipe.class)
                .setParameter("memberId", memberId)
                .setFirstResult(page * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
    }

}
