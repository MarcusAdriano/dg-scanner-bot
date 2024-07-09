package io.github.marcusadriano.dolcegustobot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public TelegramBot telegramBot(@Value("${TELEGRAM_TOKEN:}") final String token) {
        return new TelegramBot(token);
    }

    @Bean
    public ChatClient chatClient(final ChatClient.Builder builder) {
        return builder.build();
    }
}
