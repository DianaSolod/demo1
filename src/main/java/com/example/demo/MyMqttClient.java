package com.example.demo;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MyMqttClient {

    private IMqttClient client;

    public MyMqttClient(String broker, String clientId) throws MqttException {
        // create a new MQTT client instance with the broker address and client identifier
        client = new MqttClient(broker, clientId);
        // set the callback object for the client
        client.setCallback(new MyMqttCallback());
    }

    public IMqttClient getClient() {
        return client;
    }

    public void connect() throws MqttException {
        // create a new MqttConnectOptions object
        MqttConnectOptions options = new MqttConnectOptions();
        // set the clean session flag to true
        options.setCleanSession(true);
        // connect the client to the broker using the options
        client.connect(options);
    }

    public void disconnect() throws MqttException {
        // disconnect the client from the broker
        client.disconnect();
    }
}