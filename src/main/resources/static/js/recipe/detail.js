$(document).ready(function() {
    const recipeId = new URLSearchParams(window.location.search).get('id');


    if (!recipeId) {
        alert('잘못된 접근입니다.');
        location.href = '/recipe/list';
        return;
    }

    // 레시피 기본 정보 로드
    loadRecipeDetail(recipeId);

    loadIngredients(recipeId);
    loadSteps(recipeId);
    loadReviews(recipeId);

    // 좋아요 버튼
    $('#btn-like').click(function() {
        $.ajax({
            url: '/recipe/api/like',
            method: 'POST',
            data: { recipeId },
            success: function(response) {
                alert('좋아요를 눌렀습니다!');
            },
            error: function() {
                alert('좋아요 실패');
            }
        });
    });
});

function loadRecipeDetail(recipeId) {
    $.ajax({
        url: '/api/recipe/detail',  // 또는 '/recipe/api/detail'
        method: 'GET',
        data: { id: recipeId },
        success: function(recipe) {
            console.log("recipe 확인: ", recipe);

            // 기본 정보 렌더링
            $('#recipe-title').text(recipe.title);
            $('#recipe-category').text(recipe.category);
            $('#recipe-viewCount').text(recipe.viewCount || 0);
            $('#recipe-createdAt').text(formatDate(recipe.createdAt));

            // 작성자 정보
            $('#author-profile').attr('src', recipe.profile || '/images/default-profile.png');
            $('#author-name').text(recipe.loginId);

            // 대표 이미지
            if (recipe.imageUrl) {
                $('#recipe-image').attr('src', recipe.imageUrl);
            }

            // 레시피 정보
            $('#recipe-difficulty').text(recipe.difficulty || '-');
            $('#recipe-cookingTime').text(recipe.cookingTime ? recipe.cookingTime + '분' : '-');
            $('#recipe-serving').text(recipe.serving ? recipe.serving + '인분' : '-');

            // 설명
            $('#recipe-description').text(recipe.description || '설명이 없습니다.');

            // 좋아요 수
            $('#review-count').text(recipe.likeCount || 0);
        },
        error: function(xhr) {
            alert('레시피를 불러오는데 실패했습니다.');
            console.error(xhr);
            location.href = '/recipe/list';
        }
    });
}

// 날짜 포맷 함수
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
}

// 재료 로드
function loadIngredients(recipeId) {
    $.ajax({
        url: '/api/recipe/ingredients',
        method: 'GET',
        data: { recipeId },
        success: function(ingredients) {
            const $list = $('#ingredient-list');
            $list.empty();

            if (ingredients && ingredients.length > 0) {
                ingredients.forEach(item => {
                    $list.append(`
                        <div class="ingredient-item">
                            <span class="ingredient-name">${item.name}</span>
                            <span class="ingredient-amount">${item.amount}</span>
                        </div>
                    `);
                });
            } else {
                $list.append('<p>재료 정보가 없습니다.</p>');
            }
        }
    });
}

// 조리 순서 로드
function loadSteps(recipeId) {
    $.ajax({
        url: '/recipe/api/steps',
        method: 'GET',
        data: { recipeId },
        success: function(steps) {
            const $list = $('#step-list');
            $list.empty();

            if (steps && steps.length > 0) {
                steps.forEach((step, index) => {
                    $list.append(`
                        <div class="step-item">
                            <h4>Step ${index + 1}</h4>
                            <p>${step.description}</p>
                            ${step.imageUrl ? `<img src="${step.imageUrl}" alt="조리 이미지">` : ''}
                        </div>
                    `);
                });
            } else {
                $list.append('<p>조리 순서 정보가 없습니다.</p>');
            }
        }
    });
}

// 후기 로드
function loadReviews(recipeId) {
    $.ajax({
        url: '/recipe/api/reviews',
        method: 'GET',
        data: { recipeId },
        success: function(reviews) {
            const $list = $('#review-list');
            $list.empty();

            if (reviews && reviews.length > 0) {
                reviews.forEach(review => {
                    $list.append(`
                        <div class="review-item">
                            <div class="review-author">${review.loginId}</div>
                            <div class="review-content">${review.content}</div>
                            <div class="review-date">${review.createdAt}</div>
                        </div>
                    `);
                });
            } else {
                $list.append('<p>아직 후기가 없습니다.</p>');
            }
        }
    });
}