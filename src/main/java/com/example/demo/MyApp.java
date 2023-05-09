package com.example.demo;
import jakarta.servlet.ServletException;
import jakarta.websocket.DeploymentException;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.example.demo.TemperatureSensor;
import com.example.demo.MyMqttClient;
import com.example.demo.MyMqttCallback;

import java.util.Timer;
import java.util.TimerTask;

public class MyApp {

    public static void main(String[] args) throws MqttException, DeploymentException, ServletException {
        // create a new temperature sensor with the broker address, client identifier, topic and QoS
       // TemperatureSensor tempSensor = new TemperatureSensor("ssl://broker.emqx.io:8883", "myTempSensorId", "temperature", 1);
        TemperatureSensor tempSensor = new TemperatureSensor("tcp://broker.emqx.io:1883", "myTempSensorId", "temperature", 1);

        // create a new humidity sensor with the broker address, client identifier, topic and QoS
        HumiditySensor humSensor = new HumiditySensor("tcp://broker.emqx.io:1883", "myHumSensorId", "humidity", 1);
        // connect the sensors to the broker
        tempSensor.connect();
        humSensor.connect();
        // create a new temperature server with the websocket endpoint
        TemperatureServer tempServer = new TemperatureServer();
        // connect the server to the broker
        tempServer.connect();
        // subscribe to the same topic as the sensor with the same QoS
        tempServer.getClient().getClient().subscribe("temperature", 1);
        // create a timer object
        Timer timer = new Timer();

        // create a timer task object that publishes and sends a random temperature value
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // create a counter variable to keep track of the number of times the task is executed
                int counter = 0;
                // generate a random temperature value between 0 and 100
                double value = Math.random() * 100;
                // publish the temperature value from the sensor
                try {
                    tempSensor.publishTemperature(value);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                // get the temperature value from the sensor using the getter method
                value = tempSensor.getValue();
                // send the temperature value to the client as a JSON string
                tempServer.getAsyncRemote().sendText(tempServer.getGson().toJson(value));
                // increment the counter by one
                counter++;
                // if the counter reaches five, cancel the timer and disconnect the sensor and the server
                if (counter == 5) {
                    timer.cancel();
                    try {
                        tempSensor.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    try {
                        tempServer.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // schedule the timer task to run every 5 seconds
        timer.schedule(task, 0, 5000);
    }
}