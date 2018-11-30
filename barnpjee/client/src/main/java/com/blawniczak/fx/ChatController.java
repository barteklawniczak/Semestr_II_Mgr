package com.blawniczak.fx;

import com.blawniczak.service.ClientService;
import com.rabbitmq.client.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

@Component
public class ChatController implements Initializable {

    private static final String EXCHANGE_NAME = "CHAT_EXCHANGE";

    @Autowired
    private ClientService clientService;

    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField message;
    @FXML
    private ComboBox protocols;
    @FXML
    private TextArea users;

    private Connection connection;
    private Channel channel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.users.setWrapText(true);
        this.chatTextArea.setWrapText(true);
        this.setUsersInChannel();

        this.protocols.setItems(LoginController.protocolsList);
        this.protocols.getSelectionModel().select(this.clientService.getChoice());
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    if(message.startsWith("SYSTEM")) {
                        if(message.contains("left")) {
                            clientService.removeUserFromList(properties.getHeaders().get("removed").toString());
                        } else {
                            clientService.addUserToList(properties.getHeaders().get("joined").toString());
                        }
                        setUsersInChannel();
                    }
                    chatTextArea.appendText(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() throws XmlRpcException {
        if(this.clientService.getCurrentService() != null) {
            //System.out.print(this.clientService.getCurrentService());
            this.clientService.getCurrentService().sendText(this.constructMessage());
        } else {
            //System.out.print("XML_RPC");
            XmlRpcClient xmlRpcClient = this.clientService.getXmlRpcClient();
            Vector params = new Vector();
            params.addElement(this.constructMessage());
            xmlRpcClient.execute("ChatService.sendText", params);
        }
    }

    private String constructMessage() {
        return clientService.getUsername() + ": " + message.getText() + "\n";
    }

    public void changeProtocol() {
        String protocol = this.protocols.getValue().toString();
        this.clientService.applyProtocol(protocol);
        this.chatTextArea.appendText("SYSTEM: You've changed your protocol to " + protocol + "\n");
    }

    public void logout(ActionEvent event) throws IOException, TimeoutException, XmlRpcException {
        if(this.clientService.getCurrentService() != null) {
            String removed = this.clientService.getCurrentService().removeUsername(this.clientService.getUsername());
        } else {
            XmlRpcClient xmlRpcClient = this.clientService.getXmlRpcClient();
            Vector params = new Vector();
            params.addElement(this.clientService.getUsername());
            xmlRpcClient.execute("ChatService.removeUsername", params);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        this.channel.close();
        this.connection.close();
    }

    private void setUsersInChannel() {
        List<String> usernames = this.clientService.getUsernames();
        this.users.clear();
        for(String username : usernames) {
            this.users.appendText(username + "\n");
        }
    }

}
