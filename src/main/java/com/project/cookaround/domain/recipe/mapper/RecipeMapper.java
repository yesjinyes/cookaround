package com.project.cookaround.domain.recipe.mapper;

import com.project.cookaround.domain.recipe.dto.RecipeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecipeMapper {

    List<RecipeDto> showRecipeList();

    // 레시피 목록 페이지(Ajax)
    List<RecipeDto> showRecipeListJson(@Param("category") String category,
                                       @Param("sort") String sort);

    // 레시피 상세 페이지
    RecipeDto showRecipeDetail(@Param("recipeId") Long recipeId);

}
