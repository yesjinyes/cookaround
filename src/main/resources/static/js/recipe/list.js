$(document).ready(function() {
    let category = 'ALL'; // 기본 카테고리
    let sort = 'newest'; // 기본 정렬
    let currentPage = 1; // 기본 페이지

    showRecipeList(category, sort, currentPage);

    // 카테고리 클릭
    $(document).on('click', '#category-container a', function(e) {
       e.preventDefault();
       $('#category-container a').removeClass('active');
       $(this).addClass('active');

       category = $(this).data('category');
       currentPage = 1;
       showRecipeList(category, sort, currentPage);
    });

    // 정렬 클릭
    $(document).on('click', '#sort-container a', function(e) {
        e.preventDefault();
        $('#sort-container a').removeClass('active');
        $(this).addClass('active');
        sort = $(this).data('sort');
        currentPage = 1;
        showRecipeList(category, sort, currentPage);
    });

    // 페이지 클릭
    $(document).on('click', '.page-bar button', function() {
        const page = $(this).data('page');
        if (page) {
            currentPage = page;
            showRecipeList(category, sort, currentPage);
        }
    });

    // 카드 클릭
    $(document).on('click', '.recipe-card', function(e) {
        e.preventDefault(); // 추가
        const recipeId = $(this).data('id');
        console.log('Clicked recipe ID:', recipeId); // 디버깅용
        if (recipeId) {
            window.location.href = `/recipe/detail?id=${recipeId}`;
        } else {
            console.error('Recipe ID not found!');
        }
    });
});

// 목록 출력
function showRecipeList(category, sort, page) {
    $.ajax({
        url: '/recipe/api/list',
        method: 'GET',
        data: { category, sort, page },
        dataType: 'json',
        success: function(response) {
            const $container = $('#card-container');
            const $pageBar = $('.page-bar');
            $container.empty();
            $pageBar.empty();

            const data = response.list || response;
            if (!data || data.length === 0) {
                $container.append('<p>레시피가 없습니다.</p>');
                return;
            }

            data.forEach(recipe => {
                const $card = recipeByButton(recipe);
                $container.append($card);
            });

            // 페이지 버튼
            if (response.totalPages && response.totalPages > 1) {
                for (let i = 1; i <= response.totalPages; i++) {
                    const active = i === response.currentPage ? 'btn-primary' : 'btn-outline-primary';
                    $pageBar.append(`<button class="btn ${active} mx-1" data-page="${i}">${i}</button>`);
                }
            }
        },
        error: function(err) {
            console.error(err);
            alert('데이터 로딩 실패');
        }
    });
}

// 카드 생성
function recipeByButton(recipe) {
    const template = $('#recipe-list-template').html();
    const $card = $(template);

    $card.attr('data-id', recipe.id);
    $card.find('.card-img').attr('src', recipe.imageUrl || 'https://newsimg.sedaily.com/2020/11/17/1ZAFYQN80J_1.jpg');
    $card.find('.card-title').text(recipe.title);
    $card.find('.card-profile').attr('src', recipe.memberProfile || '/img/default_profile.png');
    $card.find('.card-loginId').text(recipe.loginId);

    return $card;
}
