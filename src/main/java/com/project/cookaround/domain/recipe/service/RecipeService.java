package com.project.cookaround.domain.recipe.service;

import com.project.cookaround.domain.recipe.dto.RecipeDto;
import com.project.cookaround.domain.recipe.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // final이 붙은 필드만 골라서 생성자 자동 생성
@Service
public class RecipeService {

    private final RecipeMapper recipeMapper;

    public List<RecipeDto> showRecipeList() {
        return recipeMapper.showRecipeList();
    }

    // 레시피 목록 페이지(Ajax)
    public List<RecipeDto> showRecipeListJson(String category, String sort) {
        return recipeMapper.showRecipeListJson(category, sort);
    }

    // 레시피 상세 페이지
    public RecipeDto showRecipeDetail(Long recipeId) {
        return recipeMapper.showRecipeDetail(recipeId);
    }

}
