package com.wondersgroup.qdapi.contrallor;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/web_socket")
@Component
public class WebSocketEndpoint {

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<WebSocketEndpoint> webSocketSet = new CopyOnWriteArraySet<WebSocketEndpoint>();

    private Session session;

    @OnOpen
    public void onOpen(Session session){
        this.session=session;
        WebSocketEndpoint.webSocketSet.add(this);
        WebSocketEndpoint.onlineCountAdd();
        System.out.println(session.getId()+":open!,count:"+getLineCount());
    }
    @OnClose
    public void onClose(){
        WebSocketEndpoint.webSocketSet.remove(this);
        WebSocketEndpoint.onlineCountSub();
    }
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println(session.getId()+":error");
        error.printStackTrace();
    }
    @OnMessage
    public void onMessage(String message)  {
        System.out.println(message);
        sendMessage("back:"+message);
    }

    public void sendMessage(String message){
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //群发消息
    public  void sendMessageToAll(String message){
        for(WebSocketEndpoint webSocketEndpoint:webSocketSet){
            webSocketEndpoint.sendMessage(message);
        }
    }
    public  static synchronized int  onlineCountAdd(){
        return  WebSocketEndpoint.onlineCount++;
    }
    public  static synchronized int  onlineCountSub(){
        return  WebSocketEndpoint.onlineCount--;
    }
    public  static synchronized int  getLineCount(){
        return  WebSocketEndpoint.onlineCount;
    }
}
