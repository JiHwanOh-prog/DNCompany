package com.example.dncompany.dto.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter @ToString
public class EventBoardDTO {
    Long eventId;
    String eventTitle;
    String eventContent;
    LocalDateTime eventCreatedAt;
//    LocalDateTime eventUpdatedAt;
    Long usersId;

    // 파일 정보 추가
    private Long eventImgId;
    private String eventOriginalFilename;
    private String eventUuid;
    private String eventPath;
    private String eventExtension;
    private String EventRegDate;
//    private Long EventId;

    public String getEventCreatedAt() {
        return eventCreatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    public String getEventContent() {
        String regEx = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
        if (this.eventContent == null) {
            return "";
        }
        return this.eventContent.replaceAll(regEx, "");
    }
}
