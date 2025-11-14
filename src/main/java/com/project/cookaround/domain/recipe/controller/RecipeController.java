package com.project.cookaround.domain.recipe.controller;

import com.project.cookaround.common.security.CustomUserDetails;
import com.project.cookaround.domain.recipe.dto.RecipeDto;
import com.project.cookaround.domain.recipe.dto.RecipeResponseDto;
import com.project.cookaround.domain.recipe.entity.Recipe;
import com.project.cookaround.domain.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor // final이 붙은 필드만 골라서 생성자 자동 생성
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe/list")
    public String recipeList(Model model) {
        List<RecipeDto> recipes = recipeService.showRecipeList();
        model.addAttribute("recipes",recipes);
        return "recipe/list";
    }


    // 레시피 목록 페이지(Ajax)
    @GetMapping("/recipe/api/list")
    @ResponseBody
    public List<RecipeDto> recipeListJson(@RequestParam(defaultValue = "ALL") String category,
                                          @RequestParam(defaultValue = "newest") String sort) {
        return recipeService.showRecipeListJson(category, sort);
    }


    // 레시피 상세 페이지
    @GetMapping("/recipe/detail")
    public String showRecipeDetail(String recipeId) {
        return recipeService.showRecipeDetail(recipeId);
    }


    // JPA
    // 마이페이지 - 내가 쓴 글/후기 - 레시피 목록 조회
    @ResponseBody
    @GetMapping("/api/members/mypage/recipes")
    public Map<String, Object> listByMemberId(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> response = new HashMap<>();

        // 페이징 처리
        Map<String, Object> pageSetting = recipeService.setPage(page, recipeService.getTotalCount(userDetails.getId()));
        response.put("currentPage", page);
        response.put("startPage", pageSetting.get("startPage"));
        response.put("endPage", pageSetting.get("endPage"));
        response.put("hasPrev", pageSetting.get("hasPrev"));
        response.put("hasNext", pageSetting.get("hasNext"));

        // 레시피 조회
        List<RecipeResponseDto> recipes = new ArrayList<>();
        for (Recipe recipe : recipeService.getRecipeByMemberId(userDetails.getId(), page - 1)) {
            recipes.add(RecipeResponseDto.fromEntity(recipe));
        }
        response.put("recipes", recipes);

        return response;
    }

}