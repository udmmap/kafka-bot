package org.regibot.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.regibot.models.telegram.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

@PropertySource({"classpath:token.properties","classpath:application.properties"})
@RestController
public class HookController {
    private static final Logger logger = LoggerFactory.getLogger(HookController.class);

    @Value("${kafka.topic.in}")
    private String topicIn;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public HookController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;

    }

    /*
    private void initBot(){
        String urlTelegram = "https://api.telegram.org/bot"+this.tokenBot;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = "{}";

        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(urlTelegram), HttpMethod.POST, entity, String.class);
            logger.info("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody() + " Response ="+response.getBody());
        } catch (URISyntaxException e) {
            logger.error("cant initiate hook", e);
        }

    }
    */

    @PostMapping("/regibot/hook")
    public ResponseEntity Hook(@RequestBody String upd){
        var objectMapper = new ObjectMapper();
        logger.info(upd);

        try {
            Update objUpd = objectMapper.readValue(upd, Update.class);
            kafkaTemplate.send(topicIn, objUpd.getId().toString(), upd);
        } catch (JsonProcessingException e) {
            logger.error("Can't parse Update JSON",e);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
