package io.github.aicyi.midware.message.core.sender;

import java.util.List;

public interface TextMessageSender {

    boolean sendText(List<String> toRecipients, String subject, String body);
}