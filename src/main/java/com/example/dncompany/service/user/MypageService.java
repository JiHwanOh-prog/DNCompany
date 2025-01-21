package com.example.dncompany.service.user;


import com.example.dncompany.dto.openAiChat.GeminiResponse;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.report.ReportWriteDTO;
import com.example.dncompany.dto.review.ReviewWriteDTO;
import com.example.dncompany.dto.user.mypage.*;
import com.example.dncompany.dto.user.mypage.PetImageDTO;
import com.example.dncompany.exception.user.UserNotFoundException;
import com.example.dncompany.mapper.user.MypageMapper;
import com.example.dncompany.mapper.user.MypagePetImageMapper;
import com.example.dncompany.service.openAichat.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {
    private final MypageMapper mypageMapper;
    private final MypagePetImageMapper mypagePetImageMapper;
    private final GeminiService geminiService;

    @Value("${pet.file.upload-path}")
    private String uploadPath;

    //반려동물 등록
    public void addPet(AddPetDTO addPetDTO,
                       Long usersId,
                       MultipartFile multipartFile) throws IOException {

        addPetDTO.setUsersId(usersId);
        mypageMapper.insertPet(addPetDTO);

        if (multipartFile == null || multipartFile.isEmpty()) {
            return;
        }

        String OriginalImageName = multipartFile.getOriginalFilename();
        String extension = OriginalImageName.substring(OriginalImageName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String savePath = this.uploadPath + "/" + datePath;

        PetImageDTO petImageDTO = new PetImageDTO();
        petImageDTO.setPetUuid(uuid);
        petImageDTO.setPetOriginalImageName(OriginalImageName);
        petImageDTO.setPetExtension(extension);
        petImageDTO.setPetImagePath(datePath);
        petImageDTO.setPetId(addPetDTO.getPetId());

        log.debug("petImagDTO:{}", petImageDTO);


        File uploadDir = new File(savePath);

        if (!uploadDir.exists()) {

            uploadDir.mkdirs();
        }
        //실제 저장할 파일의 이름을 uuid로 사용
        String fileSystemName = uuid + extension;
//        fileSystemName : 서버의 저장할 파일 이름, uuid.확장자
        String fileFullPath = savePath + "/" + fileSystemName;
        //fileFullPath:파일 전체 경로(이름포함), C:/upload/free/yyyy/MM/dd/파일명.확장자
        File file = new File(fileFullPath);

        //실제 파일 저장하기(실제 저장처리는 이 한줄이 끝)
        multipartFile.transferTo(file);


        mypagePetImageMapper.insertPetImg(petImageDTO);
    }


//    마이페이지

    //정보 출력
    public List<PetListDTO> selectPetList(Long usersId) {
        return mypageMapper.selectPetList(usersId);
    }

    // 수정페이지 진입시 조회
    public PetDetailDTO getPetInfoByPetId(Long petId) {
        return mypageMapper.selectByPetId(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫ID"));
    }


    public UserProfileDTO userProfile(Long usersId) {
        return mypageMapper.userProfile(usersId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원 번호 입니다"));
    }

    //사진 입력


    //상세내역 요약
    public List<HelpMeListDTO> MyPageMainHelpMeListById(Long usersId) {
        log.debug("ServiceMyPageMainHelpMeListById:{}", usersId);

        return mypageMapper.myPageMainHelpMeListById(usersId);

    }

    public List<HelpYouListDTO> MyPageMainHelpYouListById(Long usersId) {
        log.debug("ServiceMyPageMainHelpMeListById:{}", usersId);

        return mypageMapper.MyPageMainHelpYouListById(usersId);

    }


    //    정보 수정
    //    반려동물 수정
    public void modifyPetInfo(PetModifyDTO petModifyDTO, MultipartFile multipartFile) throws IOException {
        mypageMapper.updatePetInfo(petModifyDTO);

        //===============
        if (multipartFile == null || multipartFile.isEmpty()) {
            return;
        }

        mypagePetImageMapper.deleteByPetId(petModifyDTO.getPetId());

        String OriginalImageName = multipartFile.getOriginalFilename();
        String extension = OriginalImageName.substring(OriginalImageName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String savePath = this.uploadPath + "/" + datePath;

        PetImageDTO petImageDTO = new PetImageDTO();
        petImageDTO.setPetUuid(uuid);
        petImageDTO.setPetOriginalImageName(OriginalImageName);
        petImageDTO.setPetExtension(extension);
        petImageDTO.setPetImagePath(datePath);
        petImageDTO.setPetId(petModifyDTO.getPetId());

        log.debug("petImagDTO:{}", petImageDTO);


        File uploadDir = new File(savePath);

        if (!uploadDir.exists()) {

            uploadDir.mkdirs();
        }
        //실제 저장할 파일의 이름을 uuid로 사용
        String fileSystemName = uuid + extension;
//        fileSystemName : 서버의 저장할 파일 이름, uuid.확장자
        String fileFullPath = savePath + "/" + fileSystemName;
        //fileFullPath:파일 전체 경로(이름포함), C:/upload/free/yyyy/MM/dd/파일명.확장자
        File file = new File(fileFullPath);

        //실제 파일 저장하기(실제 저장처리는 이 한줄이 끝)
        multipartFile.transferTo(file);


        mypagePetImageMapper.insertPetImg(petImageDTO);
    }


    //    반려동물 삭제
    public void removePetByPetId(Long petId) {
        mypageMapper.deletePetByPetId(petId);
    }

    //회원정보
    //회원정보 출력

    public UpdateUserProfileDTO selectUserProfileById(Long usersId) {
        return mypageMapper.selectUserProfileById(usersId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원번호입니다"));

    }
    //회원정보 수정
    public void updateUserProfile(UpdateUserProfileDTO updateUserProfile) {

        if (updateUserProfile.getAddressDetail() == null) {
            updateUserProfile.setAddressDetail(""); // 기본값 설정
        }
        if (updateUserProfile.getZipCode() == null) {
            updateUserProfile.setZipCode(""); // 기본값 설정
        }

        mypageMapper.updateUserProfile(updateUserProfile);

    }


    //페이징처리

    //Qna
    public PageDTO<QnaListDTO> qnaPageList(Long usersId, PageRequestDTO pageRequestDTO) {
        List<QnaListDTO> qnaList = mypageMapper.qnaListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countQnaList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                qnaList);


    }

    //helpMe
    public PageDTO<HelpMeListDTO> helpMeListPage(Long usersId, PageRequestDTO pageRequestDTO) {
        List<HelpMeListDTO> helpMeList = mypageMapper.helpMeListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countHelpMeList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                helpMeList);


    }

    //helpYou
    public PageDTO<HelpYouListDTO> helpYouListPage(Long usersId, PageRequestDTO pageRequestDTO, Long helpId) {
        List<HelpYouListDTO> helpYouList = mypageMapper.helpYouListPage(usersId, pageRequestDTO, helpId);
        int total = mypageMapper.countHelpYouList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                helpYouList);


    }


    //zipBoard
    public PageDTO<MypageZipBoardListDTO> zipBoardListPage(Long usersId, PageRequestDTO pageRequestDTO) {
        List<MypageZipBoardListDTO> zipBoardList = mypageMapper.zipBoardListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countZipBoardList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                zipBoardList);


    }


    //zipBoard
    public PageDTO<MypageZipAnswerListDTO> zipAnswerListPage(Long usersId, PageRequestDTO pageRequestDTO) {
        List<MypageZipAnswerListDTO> zipAnswerList = mypageMapper.zipAnswerListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countZipAnswerList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                zipAnswerList);


    }

    //review
    public PageDTO<MypageReviewListDTO> reviewListPage(Long usersId, PageRequestDTO pageRequestDTO) {
        List<MypageReviewListDTO> reviewList = mypageMapper.reviewListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countReviewList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                reviewList);


    }

    public MypageReviewDetailDTO findReviewById(Long reviewId){
        return mypageMapper.selectReviewById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 리뷰ID : " + reviewId));
    }


    //dnBuy
//    public PageDTO<MypageDnBuyListDTO> mypageDnBuyListPage(Long usersId, PageRequestDTO pageRequestDTO) {
//        List<MypageDnBuyListDTO> dnBoardList = mypageMapper.mypageDnBuyListPage(usersId, pageRequestDTO);
//        int total = mypageMapper.countMypageDnBuyList(usersId);
//
//        return new PageDTO<>(pageRequestDTO.getPage(),
//                pageRequestDTO.getSize(),
//                total,
//                dnBoardList);
//
//
//    }

    //dnSell
    public PageDTO<MypageDnSellListDTO> mypageDnSellListPage(Long usersId, PageRequestDTO pageRequestDTO) {
        List<MypageDnSellListDTO> dnSellList = mypageMapper.mypageDnSellListPage(usersId, pageRequestDTO);
        int total = mypageMapper.countMypageDnSellList(usersId);

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                dnSellList);


    }

    public void modifyDnStatusByDnId(Long dnId){
        mypageMapper.updateDnStatusByDnId(dnId);
    }


    // dnLike
    public PageDTO<MypageDnLikeListDTO> getDnLikeListByUsersId(
            PageRequestDTO pageRequestDTO,
            Long usersId
    ) {
        List<MypageDnLikeListDTO> likeList = mypageMapper.selectDnLikeListByUsersId(pageRequestDTO, usersId);
        int total = mypageMapper.countDnLikeByUsersId(usersId);

        return  new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                likeList);
    }

    public boolean createReview(ReviewWriteDTO reviewWriteDTO) {
        try {
            String prompt = String.format(
                    "다음 리뷰의 감성을 분석해주세요. 별점: %d, 내용: %s\n" +
                            "**기본 로직**\n" +
                            "*대답은 꼭 GOOD,NOMAL,BAD 로만 말할 것\n" +
                            "*  **별점:**\n" +
                            "  *  5점에 가까울수록 GOOD 가능성 상승, 3점에 가까울수록 NOMAL 가능성 상승, 1점에 가까울수록 BAD 가능성 상승\n" +
                            "  *  특히 1점은 BAD 가능성을 크게 높임\n" +
                            "*  **리뷰 내용:** 텍스트 감성 분석을 통해 긍정적, 중립적, 부정적 감성 정도를 파악\n" +
                            "  *  긍정적 감성: GOOD 가능성 상승, NOMAL 가능성 약간 상승 또는 유지, BAD 가능성 하락\n" +
                            "  *  중립적 감성: NOMAL 가능성 상승, GOOD 가능성 약간 하락 또는 유지, BAD 가능성 약간 하락 또는 유지\n" +
                            "  *  부정적 감성: BAD 가능성 상승, NOMAL 가능성 하락, GOOD 가능성 하락\n" +
                            "\n" +
                            "**세부 로직 및 예시**\n" +
                            "\n" +
                            "각 조건에 따른 세부 로직과 예시를 좀 더 상세하게 설명하겠습니다.\n" +
                            "\n" +
                            "**1. GOOD 가능성 판별**\n" +
                            "\n" +
                            "*  **별점:**\n" +
                            "  *  5점: GOOD 가능성 매우 높음, NOMAL/BAD 가능성 매우 낮음\n" +
                            "  *  4점: GOOD 가능성 높음, NOMAL 가능성 약간, BAD 가능성 낮음\n" +
                            "  *  3점: GOOD 가능성 보통, NOMAL 가능성 높음, BAD 가능성 낮음\n" +
                            "  *  2점 이하: GOOD 가능성 낮음, NOMAL/BAD 가능성 높음\n" +
                            "*  **리뷰 내용:**\n" +
                            "  *  긍정적 키워드 (예: 친절, 시간 약속 잘 지킴, 만족, 최고): GOOD 가능성 상승\n" +
                            "  *  부정적 키워드 (예: 불친절, 시간 약속 안 지킴, 불만족, 최악): GOOD 가능성 하락, NOMAL/BAD 가능성 상승\n" +
                            "  *  중립적 키워드: GOOD 가능성 약간 하락 또는 유지, NOMAL 가능성 상승\n" +
                            "*  **예시:**\n" +
                            "  *  별점 5, \"시간 약속 잘 지키고 친절합니다.\": GOOD\n" +
                            "  *  별점 4, \"보너스도 주시고 친절했지만 시간 약속을 지키지 않아 아쉽습니다.\": NOMAL\n" +
                            "  *  별점 2, \"최악입니다. 노쇼했습니다.\": BAD\n" +
                            "\n" +
                            "**2. NORMAL 가능성 판별**\n" +
                            "\n" +
                            "*  **별점:**\n" +
                            "  *  3점: NOMAL 가능성 매우 높음, GOOD/BAD 가능성 보통\n" +
                            "  *  4점, 2점: NOMAL 가능성 높음, GOOD/BAD 가능성 보통\n" +
                            "  *  5점, 1점: NOMAL 가능성 낮음, GOOD/BAD 가능성 높음\n" +
                            "*  **리뷰 내용:**\n" +
                            "  *  긍정적 키워드: NOMAL 가능성 약간 상승 또는 유지, GOOD 가능성 상승\n" +
                            "  *  부정적 키워드: NOMAL 가능성 상승 또는 유지, BAD 가능성 상승\n" +
                            "  *  중립적 키워드: NOMAL 가능성 상승, GOOD/BAD 가능성 약간 하락 또는 유지\n" +
                            "*  **예시:**\n" +
                            "  *  별점 3, \"그냥 보통입니다.\": NOMAL\n" +
                            "  *  별점 4, \"친절했지만 시간 약속은 좀 아쉬웠어요.\": NOMAL\n" +
                            "  *  별점 2, \"불친절하고 시간도 안 지켰어요.\": BAD\n" +
                            "\n" +
                            "**3. BAD 가능성 판별**\n" +
                            "\n" +
                            "*  **별점:**\n" +
                            "  *  1점: BAD 가능성 매우 높음, GOOD/NOMAL 가능성 매우 낮음\n" +
                            "  *  2점: BAD 가능성 높음, NOMAL 가능성 약간, GOOD 가능성 낮음\n" +
                            "  *  3점: BAD 가능성 보통, NOMAL 가능성 높음, GOOD 가능성 낮음\n" +
                            "  *  4점 이상: BAD 가능성 낮음, GOOD/NOMAL 가능성 높음\n" +
                            "*  **리뷰 내용:**\n" +
                            "  *  긍정적 키워드: BAD 가능성 하락, GOOD/NOMAL 가능성 상승\n" +
                            "  *  부정적 키워드: BAD 가능성 상승, GOOD/NOMAL 가능성 하락\n" +
                            "  *  중립적 키워드: BAD 가능성 약간 상승 또는 유지, NOMAL 가능성 상승\n" +
                            "*  **예시:**\n" +
                            "  *  별점 1, \"최악입니다. 노쇼했습니다.\": BAD\n" +
                            "  *  별점 2, \"불친절하고 시간 약속도 안 지켰어요.\": BAD\n" +
                            "  *  별점 3, \"시간 약속은 안 지켰지만 그래도 친절하긴 했어요.\": NOMAL\n" +
                            "\n" +
                            "**결론:**\n" +
                            "\n" +
                            "*  **각 가능성(GOOD, NOMAL, BAD)은 별점과 리뷰 내용의 긍정/중립/부정 감성 정도에 따라 가중치가 부여되어 계산**\n" +
                            "*  **각 리뷰에 대한 최종 판정은 가장 높은 가능성을 가진 것으로 결정**합니다.\n" +
                            "*  **예외 상황** : 리뷰 내용이 모호하거나, 감성 분석이 어려울 경우, 별점의 가중치를 높여 판별합니다."+
                            "결과는 반드시 'GOOD', 'NORMAL', 'BAD' 중 하나로만 답변해주세요.",
                    reviewWriteDTO.getReviewStarRating(),
                    reviewWriteDTO.getReviewContent()
            );

            log.debug("AI 분석 요청: {}", prompt);
            GeminiResponse response = geminiService.generateText(prompt);
            log.debug("AI 응답: {}", response);

            String aiAssessment = "NORMAL";  // 기본값 설정

            if (response != null &&
                    response.getCandidates() != null &&
                    !response.getCandidates().isEmpty() &&
                    response.getCandidates().get(0).getContent() != null &&
                    response.getCandidates().get(0).getContent().getParts() != null &&
                    !response.getCandidates().get(0).getContent().getParts().isEmpty()) {

                String result = response.getCandidates().get(0)
                        .getContent()
                        .getParts().get(0)
                        .getText()
                        .trim()
                        .toUpperCase();

                if (Arrays.asList("GOOD", "NORMAL", "BAD").contains(result)) {
                    aiAssessment = result;
                }
            }

            reviewWriteDTO.setReviewAiAssessment(aiAssessment);
            return mypageMapper.insertReview(reviewWriteDTO) > 0;

        } catch (Exception e) {
            log.error("리뷰 등록 중 오류 발생: ", e);
            throw new RuntimeException("리뷰 등록 중 오류가 발생했습니다.", e);
        }
    }


    public void modifyHelpStatus(Long helpId, Long helpOfferId) {
        mypageMapper.updateHelpStatus(helpId, helpOfferId);
    }

    public void createReport(ReportWriteDTO reportWriteDTO) {
        mypageMapper.insertReport(reportWriteDTO);
    }
}














