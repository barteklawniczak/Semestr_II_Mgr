package com.blawniczak.service;

import com.blawniczak.ChatService;
import com.caucho.burlap.client.BurlapProxyFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class ClientService {

    private final static String hessianUrl = "http://localhost:8080/hessian_service";
    private final static String burlapUrl = "http://localhost:8080/burlap_service";
    private final static String xmlRpcUrl = "http://localhost:8080/xml-rpc";
    private ChatService hessianService = (ChatService) new HessianProxyFactory().create(ChatService.class, ClientService.hessianUrl);
    private ChatService burlapService = (ChatService) new BurlapProxyFactory().create(ChatService.class, ClientService.burlapUrl);
    private XmlRpcClient xmlRpcClient = ClientService.xmlRpcServiceMethod();
    private String username;
    private ChatService currentService;
    private int choice;
    private List<String> usernames;

    public ClientService() throws MalformedURLException { }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public ChatService getCurrentService() { return currentService; }

    public int getChoice() { return choice; }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return this.usernames;
    }

    public void applyProtocol(String protocol) {
        switch (protocol) {
            case "Hessian":
                this.setCurrentService(1);
                break;
            case "Burlap":
                this.setCurrentService(2);
                break;
            default:
                this.setCurrentService(3);
                break;
        }
    }

    private void setCurrentService(int choice) {
        this.choice = choice-1;
        switch(choice) {
            case 1:
                this.currentService = this.hessianService;
                break;
            case 2:
                this.currentService = this.burlapService;
                break;
            case 3:
                this.currentService = null; // XML - RPC
                break;
            default:
                this.currentService = this.hessianService; //set default Hessian
                break;
        }
    }

    private static XmlRpcClient xmlRpcServiceMethod() throws MalformedURLException {
        XmlRpcClientConfigImpl xmlConfig = new XmlRpcClientConfigImpl();
        xmlConfig.setServerURL(new URL(ClientService.xmlRpcUrl));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(xmlConfig);
        return client;
    }

    public XmlRpcClient getXmlRpcClient() {
        return this.xmlRpcClient;
    }

    public void removeUserFromList(String removedUser) {
        this.usernames.remove(removedUser);
    }

    public void addUserToList(String joinedUser) {
        this.usernames.add(joinedUser);
    }
}
