package com.example.dncompany.controller.user;

import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.report.ReportWriteDTO;
import com.example.dncompany.dto.review.ReviewWriteDTO;
import com.example.dncompany.dto.user.mypage.*;

import com.example.dncompany.service.sms.SmsService;
import com.example.dncompany.service.user.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    public final MypageService mypageService;
    private final SmsService smsService;


    @GetMapping("/main")
    public String mypageMain(@SessionAttribute(value = "usersId", required = false) Long usersId,
                             PageRequestDTO pageRequestDTO,
                             Model model) {


        if(usersId == null) {
            return "redirect:/user/login";
        }
        //정보출력
        List<PetListDTO> petList = mypageService.selectPetList(usersId);
//        log.info("petList: {}", petList);
        model.addAttribute("petList", petList);
        UserProfileDTO userProfile = mypageService.userProfile(usersId);
        model.addAttribute("userProfile", userProfile);

        //도와주세요

        List<HelpMeListDTO> mypageMainHelpMeList = mypageService.MyPageMainHelpMeListById(usersId);
        model.addAttribute("mainHelpMeList", mypageMainHelpMeList);
//        log.info("MypageMainHelpMeList: {}", MypageMainHelpMeList);

        LocalDateTime today = LocalDateTime.now();
        model.addAttribute("today", today);


        return "user/mypage/main";
    }


    @GetMapping("/add/pet")
    public String mypageAddPet(@SessionAttribute(value = "usersId", required = false) Long usersId) {

        if(usersId == null) {
            return "redirect:/user/login";
        }


        return "user/mypage/add-pet";
    }

    //반려동물 정보 등록 처리
    @PostMapping("/add/pet")
    public String mypageAddPet(AddPetDTO addPetDTO,
                               @SessionAttribute(value = "usersId",required = false) Long usersId,
                               @RequestParam(value = "petImg",required = false) MultipartFile multipartFile) {



        try {
            mypageService.addPet(addPetDTO,usersId,multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }


        return "redirect:/mypage/main";


    }

    ;

    @GetMapping("/update/pet")
    public String mypageUpdatePet(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                  Long petId,
                                  Model modal) {
        PetDetailDTO petDetailDTO = mypageService.getPetInfoByPetId(petId);
        modal.addAttribute("petDetailDTO", petDetailDTO);

        return "user/mypage/update-pet";
    }

    @PostMapping("/update/pet")
    public String mypageUpdatePet(PetModifyDTO petModifyDTO, @RequestParam("petImg") MultipartFile multipartFile) {
//        log.info("petModifyDTO: {}", petModifyDTO);
        try {
            mypageService.modifyPetInfo(petModifyDTO, multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return "redirect:/mypage/main";
    }


    @GetMapping("/update/profile")
    public String mypageUpdateProfile(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                      Model model) {
        UpdateUserProfileDTO selectUserProfileById = mypageService.selectUserProfileById(usersId);
        model.addAttribute("userProfile", selectUserProfileById);
        return "user/mypage/update-profile";
    }

    @PostMapping("/update/profile")
    public String mypageUpdateProfile(UpdateUserProfileDTO updateUserProfileDTO){
//        log.debug("updateUserProfileDTO: {}", updateUserProfileDTO);

        mypageService.updateUserProfile(updateUserProfileDTO);
//        log.debug("updateUserProfileDTO: {}", updateUserProfileDTO);
        return "redirect:/mypage/main";
    }

    @GetMapping("/list/zip")
    public String mypageListZip(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                PageRequestDTO pageRequestDTO,
                                Model model) {
        PageDTO<MypageZipBoardListDTO> zipBoardPageDTO= mypageService.zipBoardListPage (usersId, pageRequestDTO);
        model.addAttribute("zipBoardPageDTO", zipBoardPageDTO);

        PageDTO<MypageZipAnswerListDTO> zipAnswerPageDTO= mypageService.zipAnswerListPage (usersId, pageRequestDTO);
        model.addAttribute("zipAnswerPageDTO", zipAnswerPageDTO);
        return "user/mypage/work-list/mypage-zip-list";
    }

    @GetMapping("/list/dn")
    public String mypageListContent() {
        return "user/mypage/work-list/dn-list";
    }

    @GetMapping("/list/dn-like")
    public String mypageListDnLike(@SessionAttribute("usersId") Long usersId,
                                   PageRequestDTO pageRequestDTO,
                                   Model model) {
        PageDTO<MypageDnLikeListDTO> pageDTO = mypageService.getDnLikeListByUsersId(pageRequestDTO, usersId);
        model.addAttribute("pageDTO", pageDTO);

        return "user/mypage/work-list/dn-like";
    }

    @GetMapping("/list/review")
    public String mypageListEvent(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                  PageRequestDTO pageRequestDTO,
                                  Model model) {
        PageDTO<MypageReviewListDTO> pageDTO= mypageService.reviewListPage(usersId, pageRequestDTO);
        model.addAttribute("pageDTO", pageDTO);


        return "user/mypage/work-list/review-list";
    }

    @GetMapping("/list/helpme")
    public String mypageListHelpme(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                   PageRequestDTO pageRequestDTO,

                                   Model model) {

        PageDTO<HelpMeListDTO> pageDTO= mypageService.helpMeListPage (usersId, pageRequestDTO);
        model.addAttribute("pageDTO", pageDTO);


        return "user/mypage/work-list/helpme-list";
    }


    @GetMapping("/list/helpyou")
    public String mypageListHelpyou(@RequestParam("helpId") Long helpId,
                                    PageRequestDTO pageRequestDTO,
                                    @SessionAttribute("usersId") Long usersId,
                                    Model model
                                   ) {

        if (usersId == null || helpId == null) {
            throw new IllegalArgumentException("세션에 유효한 usersId 또는 helpId가 없습니다.");
        }

        PageDTO<HelpYouListDTO> pageDTO= mypageService.helpYouListPage (usersId,pageRequestDTO, helpId);
        model.addAttribute("pageDTO", pageDTO);

        return "user/mypage/work-list/helpyou-list";
    }

    @PostMapping("/list/helpyou")
    public String   updateHelpStatus(@RequestParam Long helpOfferId,
                                     @RequestParam Long helpId){

        try {
            mypageService.modifyHelpStatus(helpId, helpOfferId);
            // 신청자에게 문자 발송 처리
            smsService.sendAlarmToHelper(helpOfferId);
        } catch (Exception e) {
           log.error(e.getMessage());
        }


        return "redirect:/mypage/main";

    }
    @GetMapping("/list/qna")
    public String mypageListQna(@SessionAttribute(value = "usersId", required = false) Long usersId,
                                PageRequestDTO pageRequestDTO,
                                Model model) {


        PageDTO<QnaListDTO> pageDTO= mypageService.qnaPageList (usersId, pageRequestDTO);
        model.addAttribute("pageDTO", pageDTO);
        return "user/mypage/work-list/qna-list";
    }

    // 실패시 html에 상태코드가 안보여서 처리 entity 처리함
    @PostMapping("review/write")
    public ResponseEntity<String> createReview(@RequestBody ReviewWriteDTO reviewWriteDTO,
                                               @SessionAttribute(value = "usersId", required = false) Long usersId) {
        try {
            // 세션 체크
            if (usersId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // usersId 설정
            reviewWriteDTO.setUsersId(usersId);

            //서비스가 조건이 걸려있음
            // 그래서 boolean으로 판별 가능
            boolean result = mypageService.createReview(reviewWriteDTO);
            if (result) {
                return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("리뷰 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("리뷰 등록 중 오류 발생", e);  // 로그 추가
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("report/write")
    public ResponseEntity<String> createReport(@RequestBody ReportWriteDTO reportWriteDTO) {
        try {
            mypageService.createReport(reportWriteDTO);
            return ResponseEntity.ok("신고가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("신고 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }






    //    삭제 처리
    @GetMapping("/delete/pet")
    public String mypageDeletePet(Long petId) {
        mypageService.removePetByPetId(petId);
        return "redirect:/mypage/main";
    }
}
