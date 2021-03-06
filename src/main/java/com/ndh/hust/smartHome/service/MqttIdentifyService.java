package com.ndh.hust.smartHome.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mongodb.MongoException;
import com.ndh.hust.smartHome.Repository.DeviceRepository;
import com.ndh.hust.smartHome.Repository.RecordRepository;
import com.ndh.hust.smartHome.model.Device;
import com.ndh.hust.smartHome.model.Record;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@Service
@Log4j2(topic = "MQTT_IDENTIFY")
public class MqttIdentifyService extends MqttService {

    @Autowired
    private DeviceRepository devRepo;

    MqttIdentifyService() {
        super("IDENTIFY_SERVICE_ID", "MQTT_IDENTIFY_NDH");
        try {
            mqttClient.connect(options);
            mqttClient.subscribe("MQTT_IDENTIFY_NDH");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        if(mqttClient.isConnected()) {
            log.info("Ready to identify!");
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message ) {


        if (!topic.equals(subscribeTopic)) {
            log.info("Message is not in this topic!");
        }
        else {
            String msg = new String(message.getPayload());
            log.info("Receive message: " + msg);


            String userName = "";
            String passWord = "";

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(msg);
                userName = jsonNode.get("username").asText();
                passWord = jsonNode.get("password").asText();
            } catch (IOException e) {
                e.printStackTrace();
            }


            String reply = "";
            Device dev = devRepo.findByUsername(userName);

            if (dev != null && dev.getPassword().equals(passWord)) {
                reply = "{\"message\" : \"OK\"}";
            } else {
                reply = "{\"message\" : \"NO\"}";
            }

            try {
                IMqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", "a");
                MqttConnectOptions opt = new MqttConnectOptions();
                client.connect(opt);
                client.publish("MQTT_IDENTIFY_REPLY_NDH", reply.getBytes(), 0, false);
                client.disconnect();
//                mqttClient.publish("MQTT_IDENTIFY_REPLY_NDH", reply.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }

            log.info("Publish identify reply message!");

        }

    }

}