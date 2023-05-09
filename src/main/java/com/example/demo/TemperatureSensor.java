package com.example.demo;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TemperatureSensor {

    private double temperature = 20;
    private MyMqttClient myMqttClient;
    private String topic;
    private int qos;

    public TemperatureSensor(String broker, String clientId, String topic, int qos) throws MqttException {
        // create a new MQTT client with the broker address and client identifier
        myMqttClient = new MyMqttClient(broker, clientId);
        // set the topic and QoS for publishing messages
        this.topic = topic;
        this.qos = qos;
    }

    public double getValue() {
        return this.temperature; // Return the temperature value
    }


    public void connect() throws MqttException {
        // create a connection options object with some settings
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        // connect the client to the broker using the connection options
        myMqttClient.getClient().connect(options);
    }

    public void disconnect() throws MqttException {
        // disconnect the client from the broker
        myMqttClient.getClient().disconnect();
    }

    public void publishTemperature(double temperature) throws MqttException {
        this.temperature = temperature;
        // create a message with the temperature value as the payload
        MqttMessage message = new MqttMessage(String.valueOf(temperature).getBytes());
        // set the QoS and retained flag for the message
        message.setQos(qos);
        message.setRetained(true);
        // publish the message to the topic
        myMqttClient.getClient().publish(topic, message);
    }
}