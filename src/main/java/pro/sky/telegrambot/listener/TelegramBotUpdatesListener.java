package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository repository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Process your updates here
            if (update.message() == null){
                return;
            }

            if (update.message().text().equalsIgnoreCase("/start")) {
                SendMessage message = new SendMessage(update.message().chat().id(), "Hello, bounds bag");
                SendResponse response = telegramBot.execute(message);
            } else if (update.message().text().matches("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)")) {
                String dataTimeString = update.message().text().substring(0, 16);

                NotificationTask newTask = new NotificationTask(
                        update.message().chat().id(),
                        update.message().text().substring(17),
                        LocalDateTime.parse(dataTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                );

                repository.save(newTask);

                SendMessage message = new SendMessage(update.message().chat().id(), "Task is right");
                SendResponse response = telegramBot.execute(message);
            } else {
                SendMessage message = new SendMessage(update.message().chat().id(), "Неверно написана задача");
                SendResponse response = telegramBot.execute(message);
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findNowTasks() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = repository.findNotificationTasksByNotificationDate(now);
        if (!tasks.isEmpty()){
            tasks.forEach(task -> {
                SendMessage message = new SendMessage(task.getChatId(), task.getMessage());
                SendResponse response = telegramBot.execute(message);
            });
        }
    }

}
