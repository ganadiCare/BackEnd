package smCapstone.homecam.global.mqtt;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MqttGateway {

    private static final Logger log = LoggerFactory.getLogger(MqttGateway.class);
    private static final String EVENT_TOPIC = "homecam/events";

    private final MqttProperties properties;
    private final ObjectMapper objectMapper;
    private final MqttEventService mqttEventService;

    private MqttAsyncClient client;
    private volatile boolean connecting;

    @Scheduled(initialDelay = 1_000, fixedDelay = 10_000)
    public synchronized void ensureConnected() {
        try {
            if (client != null && (client.isConnected() || connecting)) {
                return;
            }

            String clientId = properties.clientId() == null || properties.clientId().isBlank()
                    ? "homecam-server"
                    : properties.clientId();

            client = new MqttAsyncClient(properties.brokerUrl(), clientId, new MemoryPersistence());
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    try {
                        client.subscribe(EVENT_TOPIC, 1);
                        log.info("MQTT connected: {}", serverURI);
                    } catch (MqttException e) {
                        log.error("MQTT event subscription failed", e);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("MQTT connection lost: {}", cause != null ? cause.getMessage() : "unknown");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    mqttEventService.handle(new String(message.getPayload(), StandardCharsets.UTF_8));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setConnectionTimeout(5);
            options.setKeepAliveInterval(30);
            if (properties.username() != null && !properties.username().isBlank()) {
                options.setUserName(properties.username());
            }
            if (properties.password() != null && !properties.password().isBlank()) {
                options.setPassword(properties.password().toCharArray());
            }
            connecting = true;
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connecting = false;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    connecting = false;
                    log.warn("MQTT connection failed: {}",
                            exception != null ? exception.getMessage() : "unknown");
                }
            });
        } catch (MqttException e) {
            connecting = false;
            log.warn("MQTT initial connection failed: {}", e.getMessage());
        }
    }

    public boolean publishRetained(String topic, Map<String, Object> payload) {
        try {
            if (client == null || !client.isConnected()) {
                log.warn("MQTT offline; retained config was not published to {}", topic);
                return false;
            }
            byte[] bytes = objectMapper.writeValueAsBytes(payload);
            client.publish(topic, bytes, 1, true);
            return true;
        } catch (Exception e) {
            log.error("MQTT publish failed: {}", topic, e);
            return false;
        }
    }

    @PreDestroy
    public void close() {
        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                client.disconnect().waitForCompletion(2_000);
            }
            client.close();
        } catch (MqttException e) {
            log.debug("MQTT close failed", e);
        }
    }
}
