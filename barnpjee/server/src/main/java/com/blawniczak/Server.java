package com.blawniczak;

import com.blawniczak.service.ChatServiceImpl;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.metadata.XmlRpcSystemImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Server {

    @Bean
    ChatService chatService() {
        return new ChatServiceImpl();
    }

    @Bean(name = "/hessian_service")
    RemoteExporter hessianService(ChatService service) {
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(chatService());
        exporter.setServiceInterface(ChatService.class);
        return exporter;
    }

    @Bean(name = "/burlap_service")
    RemoteExporter burlapService(ChatService service) {
        BurlapServiceExporter exporter = new BurlapServiceExporter();
        exporter.setService(chatService());
        exporter.setServiceInterface(ChatService.class);
        return exporter;
    }

    @Bean(name = "/xml-rpc")
    HttpRequestHandler xmlRpcService(ChatService service) throws XmlRpcException {
        PropertyHandlerMapping handlerMapping = new PropertyHandlerMapping();
        handlerMapping.setRequestProcessorFactoryFactory(pClass -> pRequest -> service);
        handlerMapping.addHandler(ChatService.class.getSimpleName(), ChatService.class);
        XmlRpcSystemImpl.addSystemHandler(handlerMapping);
        XmlRpcServletServer server = new XmlRpcServletServer();
        server.setHandlerMapping(handlerMapping);
        return server::execute;
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

}
