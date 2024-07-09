package io.github.marcusadriano.dolcegustobot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgBot implements InitializingBean {

    private final TelegramBot bot;
    private final TgProcessor processor;

    @Override
    public void afterPropertiesSet() {
        // Register for updates
        bot.setUpdatesListener(updates -> {

            for (var update : updates) {
                processor.process(update);
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
            // Create Exception Handler
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
                log.error("Error code: {} - {}", e.response().errorCode(), e.response().description(), e);
            } else {
                // probably network error
                log.error("Error (cannot decode error body)", e);
            }
        });
    }
}
