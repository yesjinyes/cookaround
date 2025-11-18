$(document).ready(function() {
    // 난이도 별점 선택
    $('.star').click(function() {
        const value = $(this).data('value');
        $('#difficulty').val(value);

        $('.star').removeClass('active');
        $(this).addClass('active');
        $(this).prevAll('.star').addClass('active');
    });

    // 별점 호버 효과
    $('.star').hover(
        function() {
            $(this).addClass('active');
            $(this).prevAll('.star').addClass('active');
            $(this).nextAll('.star').removeClass('active');
        },
        function() {
            const selectedValue = $('#difficulty').val();
            $('.star').removeClass('active');
            $(`.star[data-value="${selectedValue}"]`).addClass('active');
            $(`.star[data-value="${selectedValue}"]`).prevAll('.star').addClass('active');
        }
    );

    // 초기 별점 설정
    $('.star[data-value="1"]').addClass('active');

    // 기본 재료 3개 생성
    for(let i = 1; i < 3; i++) {
        addIngredient('ingredient');
    }

    // 드래그 앤 드롭 설정
    setupDragAndDrop('#ingredient-list');
    setupDragAndDrop('#seasoning-list');

    // 폼 제출
    $('#recipe-form').submit(function(e) {
        e.preventDefault();

        const formData = new FormData(this);

        $.ajax({
            url: '/recipe/api/create',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                alert('레시피가 등록되었습니다!');
                location.href = '/recipe/list';
            },
            error: function(err) {
                console.error(err);
                alert('등록에 실패했습니다.');
            }
        });
    });
});

// 재료/양념 추가
function addIngredient(type) {
    const listId = type === 'ingredient' ? '#ingredient-list' : '#seasoning-list';
    const nameAttr = type === 'ingredient' ? 'ingredientName[]' : 'seasoningName[]';
    const amountAttr = type === 'ingredient' ? 'ingredientAmount[]' : 'seasoningAmount[]';
    const unitAttr = type === 'ingredient' ? 'ingredientUnit[]' : 'seasoningUnit[]';
    const placeholder = type === 'ingredient' ? '재료명' : '양념명';

    const html = `
                <div class="ingredient-item" draggable="true">
                    <span class="drag-handle">☰</span>
                    <input type="text" name="${nameAttr}" placeholder="${placeholder}" required>
                    <input type="text" name="${amountAttr}" placeholder="수량" required>
                    <select name="${unitAttr}" required>
                        <option value="">단위</option>
                        <option value="g">g</option>
                        <option value="ml">ml</option>
                        <option value="개">개</option>
                        <option value="큰술">큰술</option>
                        <option value="작은술">작은술</option>
                        <option value="컵">컵</option>
                    </select>
                    <button type="button" class="btn-remove" onclick="removeItem(this)">−</button>
                </div>
            `;

    $(listId).append(html);
    setupDragAndDrop(listId);
}

// 아이템 삭제
function removeItem(btn) {
    const list = $(btn).closest('.ingredient-list');
    if (list.find('.ingredient-item').length > 1) {
        $(btn).closest('.ingredient-item').remove();
    } else {
        alert('최소 1개의 항목은 필요합니다.');
    }
}

// 드래그 앤 드롭 설정
function setupDragAndDrop(listSelector) {
    const list = document.querySelector(listSelector);
    if (!list) return;

    let draggedElement = null;

    list.querySelectorAll('.ingredient-item').forEach(item => {
        item.addEventListener('dragstart', function(e) {
            draggedElement = this;
            this.classList.add('dragging');
        });

        item.addEventListener('dragend', function(e) {
            this.classList.remove('dragging');
        });

        item.addEventListener('dragover', function(e) {
            e.preventDefault();
            const afterElement = getDragAfterElement(list, e.clientY);
            if (afterElement == null) {
                list.appendChild(draggedElement);
            } else {
                list.insertBefore(draggedElement, afterElement);
            }
        });
    });
}

function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll('.ingredient-item:not(.dragging)')];

    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;

        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}