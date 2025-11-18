package com.project.cookaround.domain.recipe.controller;

import com.project.cookaround.domain.recipe.dto.RecipeDto;
import com.project.cookaround.domain.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor // final이 붙은 필드만 골라서 생성자 자동 생성
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    // 레시피 목록 페이지
    @GetMapping("/recipe/list")
    public String recipeList(Model model) {
        List<RecipeDto> recipes = recipeService.showRecipeList();
        model.addAttribute("recipes",recipes);
        return "recipe/list";
    }

    @GetMapping("/recipe/new")
    public String newRecipe() {
        return "recipe/new";
    }

    // 레시피 목록 카테고리, 정렬 버튼 ajax
    @GetMapping("/recipe/api/list")
    @ResponseBody
    public List<RecipeDto> recipeListJson(@RequestParam(defaultValue = "ALL") String category,
                                          @RequestParam(defaultValue = "newest") String sort) {
        return recipeService.showRecipeListJson(category, sort);
    }


    // 레시피 상세 페이지
    @GetMapping("/recipe/detail")
    public String showRecipeDetail(@RequestParam("id") Long recipeId, Model model) {
        RecipeDto recipe = recipeService.showRecipeDetail(recipeId);
        model.addAttribute("recipe", recipe);
        return "recipe/detail";
    }

    // 레시피 상세 JSON 데이터 반환
    @GetMapping("/api/recipe/detail")
    @ResponseBody
    public RecipeDto getRecipeDetail(@RequestParam("id") Long recipeId) {
        return recipeService.showRecipeDetail(recipeId);
    }

}