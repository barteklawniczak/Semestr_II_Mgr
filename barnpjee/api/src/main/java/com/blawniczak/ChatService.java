package com.blawniczak;

import java.util.List;

public interface ChatService {

    String sendText(String text);
    List<String> usernameAlreadyExists(String username);
    String removeUsername(String username);
}
