package com.blawniczak.service;

import com.blawniczak.ChatService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ChatServiceImpl implements ChatService {

    private static final String EXCHANGE_NAME = "CHAT_EXCHANGE";
    private List<String> usernames = new ArrayList<String>(){{ add("SYSTEM"); }};
    private Channel channel;

    @PostConstruct
    public void initRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            Connection connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> usernameAlreadyExists(String username) {
        if(usernames.contains(username)) {
            return new ArrayList<>();
        }
        String text = "SYSTEM: User " + username + " has just joined the channel!\n";
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("joined", username);
            AMQP.BasicProperties properties =  new AMQP.BasicProperties.Builder().headers(headers).build();
            this.channel.basicPublish(EXCHANGE_NAME, "", properties, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        usernames.add(username);
        return usernames;
    }

    @Override
    public String sendText(String text) {
        try {
            this.channel.basicPublish(EXCHANGE_NAME, "", null, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Override
    public String removeUsername(String username) {
        String text = "SYSTEM: User " + username + " has just left the channel!\n";
        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("removed", username);
            AMQP.BasicProperties properties =  new AMQP.BasicProperties.Builder().headers(headers).build();
            this.channel.basicPublish(EXCHANGE_NAME, "", properties, text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.usernames.remove(username);
        return username;
    }

}
