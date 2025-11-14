$(function () {
    // AJAX POST 요청 시 CSRF 토큰을 헤더에 포함
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");
    const csrfToken = $("meta[name='_csrf']").attr("content");
    $.ajaxSetup({
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    });

    listMyRecipe("recipes", 1); // 마이페이지 첫 화면에서 조회

    // 프로필 사진 변경 모달
    $(document).on("click", "#edit-profile-btn", openModal);
    $(document).on("click", "#close-modal-btn", closeModal);

    // 새로운 프로필 사진 업로드
    $(document).on("click", "#upload-profile-btn", uploadFile);
    $(document).on("change", "#upload-file", previewNewProfile);
    
    // 프로필 사진 삭제 (기본 이미지 설정)
    $(document).on("click", "#delete-profile-btn", deleteProfile);

    // 프로필 사진 변경
    $(document).on("click", "#cancel-edit-profile-btn", closeModal);


    // 내가 쓴 글/후기 - 네비게이션 메뉴 클릭
    $(document).on("click", ".post-menu-item", function () {
        let type = $(this).data("type");
        listByType(type, 1); // 메뉴 클릭 시 항상 1페이지 조회
    });

    // 내가 쓴 글/후기 - 페이징 버튼 클릭
    $(document).on("click", ".page-btn", function () {
        let type = $(this).data("type");
        let page = $(this).data("page");
        listByType(type, page);
    });

    // 게시글 타입별 분기 처리
    function listByType(type, page) {
        if(type == "recipes") {
            listMyRecipe(type, page);
        } else if (type == "cookingTips") {
            listMyCookingTip(type, page);
        } else {
            listMyReview();
        }
    }

    // 내가 쓴 후기
    function listMyReview() {
        $.ajax({
            url: "/api/members/mypage/reviews",
            type: "GET",
            success: function (reviews) {
                $(".post-menu-item").removeClass("active");
                $("#review-list").addClass("active");
                renderReview(reviews);
            },
        });
    }

    // 내가 쓴 레시피
    function listMyRecipe(type, page) {
        $.ajax({
            url: "/api/members/mypage/recipes",
            type: "GET",
            data: {
                page: page
            },
            success: function (response) {
                $(".post-menu-item").removeClass("active");
                $("#recipe-list").addClass("active");
                renderList(response, type);
            },
        });
    }

    // 내가 쓴 요리팁
    function listMyCookingTip(type, page) {
        $.ajax({
            url: "/api/members/mypage/cooking-tips",
            type: "GET",
            data: {
                page: page
            },
            success: function (response) {
                $(".post-menu-item").removeClass("active");
                $("#cooking-tip-list").addClass("active");
                renderList(response, type);
            },
        });
    }

    // 내가 쓴 글/후기 - 게시물 목록 생성
    function renderList(response, type) {
        let table = $("#post-table-body");
        table.empty();

        // 게시글 목록
        for (const post of response[type]) {
            let row = $("<tr>");
            let id = $("<td>").text(post.id);
            let category = $("<td>").text(post.category);
            let content = $("<td>").text(post.title);
            let createdAd = $("<td>").text(post.createdAt);

            row.append(id);
            row.append(category);
            row.append(content);
            row.append(createdAd);

            table.append(row);
        }

        renderPagination(response, type); // 페이징
    }

    // 내가 쓴 글/후기 - 페이징 버튼 생성
    function renderPagination(response, type) {
        let pagination = $("#page-container");
        pagination.empty();

        let ul = $("<ul>");

        if (response.hasPrev) {
            let prev = $("<li>")
                .text("≪")
                .data("page", response.startPage - 1)
                .data("type", type)
                .addClass("page-btn");

            ul.append(prev);
        }

        for (let i = response.startPage; i <= response.endPage; i++) {
            let page = $("<li>")
                .text(i)
                .data("page", i)
                .data("type", type)
                .addClass("page-btn");

            if (response.currentPage == i) {
                page.addClass("active");
            }
            ul.append(page);
        }

        if (response.hasNext) {
            let next = $("<li>")
                .text("≫")
                .data("page", response.endPage + 1)
                .data("type", type)
                .addClass("page-btn");

            ul.append(next);
        }

        pagination.append(ul);
    }

    // 내가 쓴 글/후기 - 동적 HTML 태그 생성 (후기)
    function renderReview(reviews) {
        let table = $("#post-table-body");
        table.empty();

        for (const review of reviews) {
            let row = $("<tr>");
            let id = $("<td>").text(review.id);
            let category = $("<td>").text(review.category);
            let content = $("<td>").text(review.content);
            let createdAd = $("<td>").text(review.createdAt);

            row.append(id);
            row.append(category);
            row.append(content);
            row.append(createdAd);

            table.append(row);
        }
    }

    // 내가 쓴 글/후기 - 동적 HTML 태그 생성 (요리팁, 레시피)
    function renderPost(posts) {
        let table = $("#post-table-body");
        table.empty();

        for (const post of posts) {
            let row = $("<tr>");
            let id = $("<td>").text(post.id);
            let category = $("<td>").text(post.category);
            let title = $("<td>").text(post.title);
            let createdAd = $("<td>").text(post.createdAt);

            row.append(id);
            row.append(category);
            row.append(title);
            row.append(createdAd);

            table.append(row);
        }
    }


    // 모달 열기
    function openModal() {
        $("#modal-background, #modal-container").fadeIn();
    }

    // 모달 닫기
    function closeModal() {
        $("#modal-background, #modal-container").fadeOut();
    }

    // input #upload-file 실행
    function uploadFile() {
        $("#upload-file").click();
    }

    // 프로필 사진 편집
    function previewNewProfile() {
        let newProfile = this.files;
        let imageUrl = URL.createObjectURL(newProfile[0]);
        $("#modal-profile img").attr("src", imageUrl);
    }

    // 프로필 사진 삭제 (기본 이미지 설정)
    function deleteProfile() {
        $("#modal-profile img").attr("src", "/uploads/profile/default.jpg");
        $("#default-profile").val(true);
    }

});