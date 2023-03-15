package pro.sky.telegrambot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String message;
    private LocalDateTime notificationDate;

    public NotificationTask(Long chatId, String message, LocalDateTime notificationDate) {
        this.chatId = chatId;
        this.message = message;
        this.notificationDate = notificationDate;
    }

    public NotificationTask(){};

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Long getChatId(){return chatId;}
}
