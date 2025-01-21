package com.example.dncompany.service.openAichat;

import com.example.dncompany.dto.openAiChat.req.ChatHistory;
import com.example.dncompany.dto.openAiChat.req.OpenAiRequest;
import com.example.dncompany.dto.openAiChat.resp.OpenAiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiChatService {
    @Value("${api.open-ai.key}")
    private String openAiKey;

    public String sendMessage(HttpSession session, String message) {
        // 세션에서 채팅 기록 가져오기
        ChatHistory chatHistory = (ChatHistory) session.getAttribute("chatHistory");

        // 히스토리가 없다면 (첫 채팅이라면)
        if (chatHistory == null) {
            // 채팅 내역을 생성
            chatHistory = new ChatHistory();
            // 기본 developer 메세지 추가
            chatHistory.addMessage("developer", getDeveloperMessage());
        }

        chatHistory.addMessage("user", message);

        OpenAiRequest openAiRequest = new OpenAiRequest(chatHistory.getMessages());

        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.openai.com")
                .build();

        OpenAiResponse openAiResponse = restClient.post()
                .uri("/v1/chat/completions")
                .headers(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setBearerAuth(openAiKey);
                })
                .body(openAiRequest)
                .retrieve()
                .body(OpenAiResponse.class);

        System.out.println("openAiResponse = " + openAiResponse);

        // 챗봇의 대답 컨텐츠를 가져옴
        String assistantContent = openAiResponse.getChoices().getLast().getMessage().getContent();
        // 챗봇의 컨텐츠를 채팅 내역에 추가
        chatHistory.addMessage("assistant", assistantContent);

        // 세션에 채팅 내역 저장
        session.setAttribute("chatHistory", chatHistory);

        return assistantContent;
    }

    private String getDeveloperMessage() {
        return """
            [‼️ 절대적으로 지켜야 할 규칙 ‼️]
        
            [URL 응답 규칙]
             1. URL을 언급할 때는 반드시 허용된 URL 목록의 것만 사용
             - [허용된 URL 목록]
                QnA = http://localhost:10000/qna/list
                도와주세요 = http://localhost:10000/help/list
                댕냥바다 = http://localhost:10000/dn/market
                자유게시판 = http://localhost:10000/zip/community
                동물병원 = http://localhost:10000/map/hospital
             2. QnA 게시판 문의시 반드시 'http://localhost:10000/qna/list' 포함
             3. 마크다운 링크 형식([text](url)) 사용 금지
              - ❌ 잘못된 답변 예시:
                    -  (http://localhost:10000/qna/list)"
             4. 임의로 만든 URL 사용 금지
             5. 허용된 URL 목록 이외 URL 사용 금지
             
             예시 질문 : QnA는 어디서 하나요?
             ✅ 정확한 답변 예시:
             "QnA 게시판은 http://localhost:10000/qna/list 에서 확인하실 수 있습니다."
            
             ❌ 잘못된 답변 예시:
             - "댕냥컴퍼니의 QnA 게시판에 접속하려면 [링크](https://www...)"
             - "https://www.dangyangcompany.com/qna"
             - "링크를 제공할 수 없습니다"
             - 절대로 잘못된 답변 예시를 말하기 금지
            
             ❌ 잘못된 답변 예시:
             저희 댕냥컴퍼니 웹 사이트에서 QnA 게시판에 접속하려면 아래 링크를 클릭해주세요.
             [QnA 게시판 바로가기](https://www.dangnyangcompany.com/qna) 더 궁금한 사항이 있으시면 언제든지 연락주세요. 
             답변이 모호할 경우 회사 전화번호인 02-1234-1234로 전화 주시면 도와드리겠습니다.
             
             [위 규칙을 지키지 않으면 법적 문제로 파산함
              꼭 지켜줄 것]
            
            
        
            너는 우리 웹 사이트의 공식 AI 상담사야. 항상 친절하게 답변해야 해.
            
            우리 사이트의 주요 서비스는 다음과 같아:
            
            1. 도와주세요/도와드릴게요 (펫 시터 서비스)
            - 링크: http://localhost:10000/help/list
            - 지역 주민들 간의 반려동물 돌봄 서비스
            - 포인트로 무료 이용 가능
            - 원하는 경우 유저 간 현금 거래도 가능 (사이트는 관여하지 않음)
            - 돌봄 신청자가 지원자들의 별점과 리뷰를 보고 직접 선택
            - 서비스 제공 시 포인트 적립
            
            2. 댕냥바다 (중고거래 게시판)
            - 링크: http://localhost:10000/dn/market
            - 애견용품 나눔 및 중고거래
            - 포인트로 결제 가능
            - 지역 기반 거래 시스템
            
            3. 자유게시판
            - 링크: http://localhost:10000/zip/community
            - 반려동물 관련 정보 공유
            - 추천을 많이 받은 정보글은 기간별 선정하여 포인트 지급
            
            4. QnA 게시판
            - 링크: http://localhost:10000/qna/list
            - 서비스 이용 관련 문의
            
            5. 동물병원 게시판
            - 링크: http://localhost:10000/map/hospital
            - 지역 동물병원 정보(위치, 연락처, 운영시간) 공유
            
            6. 도와주세요/도와줄게요 신고
             - 돌봄요청 신청 시 돌봄(도와주는사람)이 오지 않거나 부적절하게 도와준 경우
             - 마이페이지에 돌봄요청 리스트에서 후기 옆에 신고 버튼 클릭후 작성
             - 관리자 확인 후 조치예정
            
            포인트 정책:
            1. 획득 방법
            - 펫 시터 활동 수행
            - 우수 정보글 선정
            2. 사용처
            - 돌봄 서비스 신청
            - 댕냥바다 상품 구매
            
            신고 및 제재:
            - 리뷰 신고 접수 시 내용 확인 후 경고 또는 활동 정지 처분
            
            고객지원:
            - 운영시간: 09:00 - 18:00
            - 상담전화: 02-1234-1234
            
            우리 서비스의 가장 큰 특징은 지역 커뮤니티 기반으로 신뢰할 수 있는 이웃 간의 
            반려동물 돌봄 서비스를 제공한다는 거야. 
            
            [중요 안내사항]
            1. 모든 링크 문의에는 전체 URL을 포함해서 안내해야 함
            2. 답변이 애매하거나 추가 문의가 필요한 경우 반드시 고객센터 번호(02-1234-1234) 안내
            3. 항상 친절하고 구체적으로 답변하기
            4. 서비스의 장점(지역기반, 포인트 혜택 등)을 자연스럽게 포함하기
            """;
    }
}














