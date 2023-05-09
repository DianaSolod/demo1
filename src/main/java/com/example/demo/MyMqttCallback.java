package com.example.demo;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        // handle connection lost event
        System.out.println("Connection lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // handle message arrived event
        System.out.println("Message arrived on topic " + topic + ": " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // handle delivery complete event
        System.out.println("Delivery complete for token: " + token.getMessageId());
    }
}