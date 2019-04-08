package com.ndh.hust.smartHome.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndh.hust.smartHome.model.Command;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Service
@Log4j2(topic = "MQTT_CONTROL")
public class MqttControlService extends MqttService {

    MqttControlService() {
        super("CONTROL_SERVICE_ID", "A");
        if(mqttClient.isConnected()) {
            log.info("Connected. Ready to send command!");
        }
    }

    public void publishCommand(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        String commandJson = null;

        try {
            commandJson = objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            mqttClient.publish("MQTT_CONTROL_NDH", commandJson.getBytes(), 0, true);
            log.info(commandJson);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
