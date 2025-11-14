package com.project.cookaround.domain.recipe.service;

import com.project.cookaround.domain.recipe.dto.RecipeDto;
import com.project.cookaround.domain.recipe.entity.Recipe;
import com.project.cookaround.domain.recipe.mapper.RecipeMapper;
import com.project.cookaround.domain.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor // final이 붙은 필드만 골라서 생성자 자동 생성
@Service
public class RecipeService {

    private final RecipeMapper recipeMapper; // MyBatis
    private final RecipeRepository recipeRepository; // JPA

    private static final int PAGE_SIZE = 10; // 한 페이지에 보여줄 요리팁 개수
    private static final int BLOCK_SIZE = 5; // 한 번에 보여줄 페이징 버튼 개수

    public List<RecipeDto> showRecipeList() {
        return recipeMapper.showRecipeList();
    }

    // 레시피 목록 페이지(Ajax)
    public List<RecipeDto> showRecipeListJson(String category, String sort) {
        return recipeMapper.showRecipeListJson(category, sort);
    }

    // 레시피 상세 페이지
    public String showRecipeDetail(String recipeId) {
        return recipeMapper.showRecipeDetail(recipeId);
    }

    // 마이페이지 - 내가 쓴 글/후기 - 레시피 개수 조회
    public Long getRecipeCountByMemberId(Long memberId) {
        Long cnt = recipeRepository.countByMemberId(memberId);
        if (cnt != 0) {
            return cnt;
        }
        return 0L;
    }

    // 마이페이지 - 내가 쓴 글/후기 - 레시피 조회
    public List<Recipe> getRecipeByMemberId(Long memberId, int page) {
        return recipeRepository.findByMemberIdOrderByIdDesc(memberId, page, PAGE_SIZE);
    }

    // 페이징 처리
    public Map<String, Object> setPage(int page, Long totalCount) {
        int totalPage = (int) Math.ceil((double) totalCount / PAGE_SIZE); // 전체 페이지 수, 소수점 올림

        // 페이징 버튼
        int currentBlock = (int) Math.ceil((double) page / BLOCK_SIZE); // page가 위치한 블럭
        int startPage = (currentBlock - 1) * BLOCK_SIZE + 1; // 현재 블록에서 시작하는 페이지 번호
        int endPage = Math.min(startPage + BLOCK_SIZE - 1, totalPage); // 현재 블록에서 끝나는 페이지 번호

        boolean hasPrev = startPage > 1;
        boolean hasNext = endPage < totalPage;

        Map<String, Object> pageSetting = new HashMap<>();
        pageSetting.put("startPage", startPage);
        pageSetting.put("endPage", endPage);
        pageSetting.put("hasPrev", hasPrev);
        pageSetting.put("hasNext", hasNext);

        return pageSetting;
    }

    // 마이페이지 내가 쓴 글/후기 - 회원별 레시피 개수 조회
    public Long getTotalCount(Long memberId) {
        return recipeRepository.countByMemberId(memberId);
    }

}
