package com.project.cookaround.domain.cookingtip.service;

import com.project.cookaround.domain.cookingtip.entity.CookingTip;
import com.project.cookaround.domain.cookingtip.entity.CookingTipCategory;
import com.project.cookaround.domain.cookingtip.repository.CookingTipRepository;
import com.project.cookaround.domain.image.entity.ImageContentType;
import com.project.cookaround.domain.image.repository.ImageRepository;
import com.project.cookaround.domain.image.service.ImageService;
import com.project.cookaround.domain.likes.entity.Likes;
import com.project.cookaround.domain.likes.entity.LikesContentType;
import com.project.cookaround.domain.likes.repository.LikesRepository;
import com.project.cookaround.domain.member.entity.Member;
import com.project.cookaround.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class CookingTipService {

    private final CookingTipRepository cookingTipRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final LikesRepository likesRepository;
    private final ImageService imageService;

    private static final int PAGE_SIZE = 10; // 한 페이지에 보여줄 요리팁 개수
    private static final int BLOCK_SIZE = 5; // 한 번에 보여줄 페이징 버튼 개수

    public CookingTipService(CookingTipRepository cookingTipRepository, MemberRepository memberRepository,
                             ImageRepository imageRepository, LikesRepository likesRepository, ImageService imageService) {
        this.cookingTipRepository = cookingTipRepository;
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
        this.likesRepository = likesRepository;
        this.imageService = imageService;
    }


    // 요리팁 조회 - 전체
    public List<CookingTip> getCookingTipsBySortAndCategory(String sort, String category, int page) {
        if (category.equals("ALL")) {
            if (sort.equals("LATEST")) {
                return cookingTipRepository.findAllOrderByCreatedAtDesc(page, PAGE_SIZE);
            } else if (sort.equals("VIEWS")) {
                return cookingTipRepository.findAllOrderByViewCountDesc(page, PAGE_SIZE);
            } else if (sort.equals("LIKES")) {
                return cookingTipRepository.findAllOrderByLikeCountDesc(page, PAGE_SIZE);
            } else {
                return null;
            }
        } else {
            CookingTipCategory cookingTipCategory = CookingTipCategory.fromString(category);
            if (sort.equals("LATEST")) {
                return cookingTipRepository.findByCategoryOrderByCreatedAtDesc(cookingTipCategory, page, PAGE_SIZE);
            } else if (sort.equals("VIEWS")) {
                return cookingTipRepository.findByCategoryOrderByViewCountDesc(cookingTipCategory, page, PAGE_SIZE);
            } else if (sort.equals("LIKES")) {
                return cookingTipRepository.findByCategoryOrderByLikeCountDesc(cookingTipCategory, page, PAGE_SIZE);
            } else {
                return null;
            }
        }
    }

    // 페이징 처리
    public Map<String, Object> setPage(int page, Long totalCount) {
        int totalPage = (int) Math.ceil((double) totalCount / PAGE_SIZE); // 전체 페이지 수, 소수점 올림

        // 페이징 버튼
        int currentBlock = (int) Math.ceil((double) page / BLOCK_SIZE); // page가 위치한 블럭
        /**
         * page=7, blockSize=5
         * 7 ÷ 5 = 1.4 → 올림 → currentBlock = 2
         * 7페이지는 “두 번째 블록(6~10)”에 속함.
         */

        int startPage = (currentBlock - 1) * BLOCK_SIZE + 1; // 현재 블록에서 시작하는 페이지 번호
        /**
         * currentBlock=2, blockSize=5
         * (2-1) × 5 + 1 = 6 → startPage=6
         * 두 번째 블록은 6부터 시작.
         */

        int endPage = Math.min(startPage + BLOCK_SIZE - 1, totalPage); // 현재 블록에서 끝나는 페이지 번호
        /**
         * 기본 startPage + 4 (5개 단위)
         * 전체 페이지(totalPage)보다 넘어가면 안 되므로 Math.min으로 보정.
         * 전체 페이지가 23이고, startPage=21이면
         * 21+5-1=25 → min(25, 23) = 23 → endPage=23
         */

        boolean hasPrev = startPage > 1;
        boolean hasNext = endPage < totalPage;

        Map<String, Object> pageSetting = new HashMap<>();
        pageSetting.put("startPage", startPage);
        pageSetting.put("endPage", endPage);
        pageSetting.put("hasPrev", hasPrev);
        pageSetting.put("hasNext", hasNext);

        return pageSetting;
    }

    // 요리팁 목록 화면에서 사용 - 카테고리별 요리팁 개수 조회
    public Long getTotalCount(String category) {
        if (category.equals("ALL")) {
            return cookingTipRepository.countAll();
        } else {
            return cookingTipRepository.countByCategory(CookingTipCategory.fromString(category));
        }
    }

    // 마이페이지 내가 쓴 글/후기 화면에서 사용 - 회원별 요리팁 개수 조회
    public Long getTotalCount(Long memberId) {
        return cookingTipRepository.countByMemberId(memberId);
    }

    // 요리팁 조회 - 카테고리
    public List<CookingTip> getCookingTipsByCategory(CookingTipCategory category) {
        return cookingTipRepository.findByCategory(category);
    }

    // 요리팁 조회 - 상세
    @Transactional
    public CookingTip getCookingTipDetail(Long id) {
        CookingTip cookingTip = cookingTipRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("요리팁을 찾을 수 없습니다."));

        cookingTip.increaseViewCount();

        return cookingTip;
    }

    // 요리팁 등록
    @Transactional
    public Long registerCookingTip(Long memberId, CookingTip cookingTip) {
        Member member = memberRepository.findOne(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        cookingTip.setMember(member);
        cookingTipRepository.save(cookingTip);

        return cookingTip.getId();
    }

    // 요리팁 수정
    @Transactional
    public Long updateCookingTip(Long memberId, CookingTip cookingTip) {
        CookingTip foundCookingTip = cookingTipRepository.findById(cookingTip.getId())
                .orElseThrow(() -> new NoSuchElementException("요리팁을 조회할 수 없습니다."));

        foundCookingTip.setCategory(cookingTip.getCategory());
        foundCookingTip.setTitle(cookingTip.getTitle());
        foundCookingTip.setDescription(cookingTip.getDescription());
        foundCookingTip.setContent(cookingTip.getContent());

        return foundCookingTip.getId();
    }

    // 요리팁 삭제
    @Transactional
    public boolean deleteCookingTip(Long cookingTipId) {
        // 요리팁 삭제
        CookingTip cookingTip = cookingTipRepository.findById(cookingTipId)
                .orElseThrow(() -> new NoSuchElementException("요리팁을 조회할 수 없습니다."));
        cookingTipRepository.delete(cookingTip);

        // 이미지 삭제
        List<Long> imageIds = imageRepository.findIdsByContentTypeAndContentId(ImageContentType.COOKINGTIP, cookingTipId);
        imageService.deleteImages(imageIds, ImageContentType.COOKINGTIP, cookingTipId);

        // 좋아요 삭제
        List<Likes> likes = likesRepository.findByContentTypeAndContentId(LikesContentType.COOKINGTIP, cookingTipId);
        for (Likes like : likes) {
            likesRepository.delete(like);
        }

        // 삭제 확인
        Optional<CookingTip> deletedCookingTip = cookingTipRepository.findById(cookingTipId);
        return deletedCookingTip.isEmpty();
    }

    // 마이페이지 - 내가 쓴 글/후기, 요리팁 개수 출력
    public Long getCookingTipCountByMemberId(Long memberId) {
        Long cnt = cookingTipRepository.countByMemberId(memberId);
        if (cnt != 0) {
            return cnt;
        }
        return 0L;
    }

    // 마이페이지 - 내가 쓴 글/후기 - 요리팁 조회
    public List<CookingTip> getCookingTipByMemberId(Long memberId, int page) {
        return cookingTipRepository.findByMemberIdOrderByIdDesc(memberId, page, PAGE_SIZE);
    }
}
