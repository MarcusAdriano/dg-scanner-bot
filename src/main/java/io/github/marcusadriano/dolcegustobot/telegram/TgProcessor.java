package io.github.marcusadriano.dolcegustobot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Component
public class TgProcessor {

    private final ChatClient chatClient;
    private final TelegramBot bot;

    @SneakyThrows
    @Async
    public Future<Void> process(final Update update) {

        final Message message = update.message();
        if (message.photo() != null && message.photo().length > 0) {

            final String fileId = message.photo()[message.photo().length - 1].fileId();
            final GetFile request = new GetFile(fileId);
            final GetFileResponse response = bot.execute(request);

            final File file = response.file();
            final String fullFilePath = bot.getFullFilePath(file);
            final List<Media> photos = List.of(new Media(MimeTypeUtils.IMAGE_PNG, URI.create(fullFilePath).toURL()));

            final UserMessage userMessage = new UserMessage("Please, tell me what are characters in the photo. Your response must contain only the chars.", photos);

            final Characters chars = chatClient.prompt()
                .messages(userMessage)
                .call()
                .entity(Characters.class);

            log.info("Chat Response: {}", chars);

            final long chatId = update.message().chat().id();
            final int replyToMessageId = update.message().messageId();
            sendMessage(chatId, chars.chars(), replyToMessageId);

        } else {

            sendMessage(message.chat().id(), "I'm sorry, you cannot use this resource!!");

        }

        return CompletableFuture.completedFuture(null);
    }

    record Characters(String chars) {

    }

    private void sendMessage(final Long chatId, final String content) {
        sendMessage(chatId, content, null);
    }

    private void sendMessage(final Long chatId, final String content, final Integer messageId) {

        if (content != null && content.isBlank()) {
            log.warn("Content is empty!");
            return;
        }

        final SendMessage msg = new SendMessage(chatId, content);
        if (messageId != null) {
            msg.replyToMessageId(messageId);
        }
        SendResponse response = bot.execute(msg);

        log.info("Message send response: {}", response.isOk());
    }
}
