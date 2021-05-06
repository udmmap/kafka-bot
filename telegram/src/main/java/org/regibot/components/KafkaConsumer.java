package org.regibot.components;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@PropertySource({"classpath:token.properties","classpath:application.properties"})
@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final RestTemplate restTemplate;

    @Value( "${bot.token}" )
    private String tokenBot;

    @Value( "${telegram.url}" )
    private String urlTelegram;

    @Autowired
    public KafkaConsumer(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @KafkaListener(topics = "${kafka.topic.out}")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        logger.info("received ='{}'", consumerRecord.value());

        String urlApi = urlTelegram+"/bot"+this.tokenBot+"/sendMessage";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var entity = new HttpEntity(consumerRecord.value(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(urlApi), HttpMethod.POST, entity, String.class);
            logger.info("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody() + " Response ="+response.getBody());
        } catch (Exception e) {
            logger.error("can't send answer", e);
        }

    }

}
