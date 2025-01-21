package com.example.dncompany.mapper.user;

import com.example.dncompany.dto.openAiChat.GeminiResponse;
import com.example.dncompany.dto.user.mypage.*;

import com.example.dncompany.service.openAichat.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Slf4j
@Transactional
class MypageMapperTest {

    @Autowired
    MypageMapper mypageMapper;
    AddPetDTO addPetDTO;
    HelpYouListDTO helpYouListDTO;

    @Autowired
    private GeminiService geminiService;

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

    @BeforeEach
    void setUp() {
        addPetDTO = new AddPetDTO();
//        addPetDTO.setPetId(6L);
        addPetDTO.setPetName("테스트");
        addPetDTO.setPetSpecies("강아지");
        addPetDTO.setPetGender("F");
        addPetDTO.setUsersId(6L);

        addPetDTO.setPetBirthDate(LocalDate.of(1990, 1, 1));
        addPetDTO.setAdoptionDate(LocalDate.of(1990, 1, 1));


    }

    @Test
    void insertPet() {
        mypageMapper.insertPet(addPetDTO);

        List<PetListDTO> petList = mypageMapper.selectPetList(6L);

        assertThat(petList)
                .isNotEmpty()
                .extracting("petName")
                .contains("테스트");
    }


    @Test
    void userProfile() {
        Optional<UserProfileDTO> userProfile = mypageMapper.userProfile(21L);


    }

    @Test
    void helpMeListById() {


    }

    @Test
    void mypageMainList() {

        List<HelpMeListDTO> helpMeList = mypageMapper.myPageMainHelpMeListById(81L);

        assertThat(helpMeList)
                .isNotEmpty();
        log.debug(helpMeList.toString());
    }


    @Test
    void updateHelpStatus() {
        mypageMapper.updateHelpStatus(142L,161L);


    }

    @Test
    void testGeminiAPI() {
        log.info("=== Gemini API 테스트 시작 ===");

        String prompt = "다음 리뷰의 감성을 분석해주세요. 별점: 5, 내용: 정말 좋았어요\n" +
                "결과는 'GOOD', 'NORMAL', 'BAD' 중 하나로만 답변해주세요.";

        log.info("API 호출 시작: {}", prompt);

        try {
            GeminiResponse response = geminiService.generateText(prompt);
            log.info("API 응답: {}", response);

            assertNotNull(response, "응답이 null이면 안됩니다");
            assertNotNull(response.getCandidates(), "candidates가 null이면 안됩니다");
            assertFalse(response.getCandidates().isEmpty(), "candidates가 비어있으면 안됩니다");

            String result = response.getCandidates().get(0)
                    .getContent()
                    .getParts().get(0)
                    .getText();
            log.info("AI 분석 결과: {}", result);

        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: ", e);
            fail("API 호출 실패: " + e.getMessage());
        }

        log.info("=== Gemini API 테스트 완료 ===");
    }
}









