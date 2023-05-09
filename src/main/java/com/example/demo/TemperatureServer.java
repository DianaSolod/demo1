package com.example.demo;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.websocket.*;
import jakarta.websocket.server.*;
import java.util.*;
import com.google.gson.*;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.paho.client.mqttv3.*;

@ServerEndpoint("/temperature") public class TemperatureServer {

    private MyMqttClient myMqttClient; // A private variable to store the MQTT client object
    private Session session; // A private variable to store the websocket session object
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TemperatureServer() throws MqttException {
        // create a new MQTT client with the broker address and client identifier
        myMqttClient = new MyMqttClient("tcp://broker.emqx.io:1883", "TemperatureServer");
        // set the callback object for the client
        myMqttClient.getClient().setCallback(new MqttCallback() {
            public void connectionLost(Throwable cause) {
                // handle connection lost event
                System.out.println("Connection lost: " + cause.getMessage());
            }

            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // handle message arrived event
                System.out.println("Message arrived on topic " + topic + ": " + new String(message.getPayload()));
                // send the message payload to the websocket session as a text message
                session.getBasicRemote().sendText(new String(message.getPayload()));
            }


            public void deliveryComplete(IMqttDeliveryToken token) {
                // handle delivery complete event
                System.out.println("Delivery complete for token: " + token.getMessageId());
            }
        });
    }

    public RemoteEndpoint.Async getAsyncRemote() {
        // return the asynchronous remote endpoint for the websocket session
        return session.getAsyncRemote();
    }

    @OnOpen
    public void onOpen(Session session) throws MqttException {
        // store the websocket session object
        this.session = session;
        // connect the MQTT client to the broker
        myMqttClient.connect();
        // subscribe to the same topic as the sensor with the same QoS
        myMqttClient.getClient().subscribe("temperature", 1);
    }

    public MyMqttClient getClient() {
        // return the MQTT client object
        return myMqttClient;
    }

    public void connect() throws MqttException {
        // connect the MQTT client to the broker
        myMqttClient.connect();
    }

    @OnClose
    public void onClose(Session session) throws MqttException {
        // disconnect the MQTT client from the broker
        myMqttClient.disconnect();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // handle websocket error event
        System.out.println("Error: " + throwable.getMessage());
    }

    public Gson getGson() {
        // return the Gson object for JSON serialization
        return gson;
    }

    public void disconnect() throws MqttException {
        // disconnect the MQTT client from the broker
        myMqttClient.disconnect();
    }
}