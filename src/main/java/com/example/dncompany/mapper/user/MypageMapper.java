package com.example.dncompany.mapper.user;


import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.report.ReportWriteDTO;
import com.example.dncompany.dto.review.ReviewWriteDTO;
import com.example.dncompany.dto.user.mypage.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Optional;


@Mapper
public interface MypageMapper {

    void insertPet(AddPetDTO addPetDTO);

    //마이페이지 메인

    //정보출력
    List<PetListDTO> selectPetList(Long usersId);



    // 수정페이지 진입시 조회
    Optional<PetDetailDTO> selectByPetId(Long petId);

    Optional<UserProfileDTO> userProfile(Long usersId);


    //게시글 요약

    List<HelpMeListDTO> myPageMainHelpMeListById(Long usersId);

    List<HelpYouListDTO> MyPageMainHelpYouListById(Long usersId);


    //    정보 수정
//    반려동물 수정
    void updatePetInfo(PetModifyDTO petModifyDTO);

    //    회원정보
//    회원정보 불러오기
    Optional<UpdateUserProfileDTO> selectUserProfileById(Long usersId);

    //    회원정보 수정
    void updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO);

    //    반려동물 삭제
    void deletePetByPetId(Long petId);





    //페이지처리

    //Qna
    List<QnaListDTO> qnaListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countQnaList(Long usersId);

    //HelpMe
    List<HelpMeListDTO> helpMeListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countHelpMeList(Long usersId);

    //Helpyou
    List<HelpYouListDTO> helpYouListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO,
                                         @Param("helpId") Long helpId
                                        );

    int countHelpYouList(Long usersId);

    //zipBoard
    List<MypageZipBoardListDTO> zipBoardListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countZipBoardList(Long usersId);

    //zipAnswer
    List<MypageZipAnswerListDTO> zipAnswerListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countZipAnswerList(Long usersId);

    //review
    List<MypageReviewListDTO> reviewListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countReviewList(Long usersId);

    Optional<MypageReviewDetailDTO> selectReviewById(Long reviewId);



    //dnSell
    List<MypageDnSellListDTO> mypageDnSellListPage(@Param("usersId") Long usersId,
                                 @Param("page") PageRequestDTO pageRequestDTO);

    int countMypageDnSellList(Long usersId);

    void updateDnStatusByDnId(Long dnId);

    // dnLike
    List<MypageDnLikeListDTO> selectDnLikeListByUsersId(@Param("page") PageRequestDTO pageRequestDTO,
                                                        @Param("usersId") Long usersId);

    int countDnLikeByUsersId(Long usersId);

    int insertReview(ReviewWriteDTO reviewWriteDTO);

    void insertReport(ReportWriteDTO reportWriteDTO);


    void updateHelpStatus(@Param("helpId") Long helpId, @Param("helpOfferId") Long helpOfferId);


}



















