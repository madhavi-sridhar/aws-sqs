package com.prasan.controller;

import com.prasan.dto.Person;
import com.prasan.queue.QueueListner;
import com.prasan.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RestController
public class SqsController implements QueueListner {

    @Autowired
    MyService service;

    @Autowired
    QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}")
    private String endPoint;

    @PostMapping("/send")
    public void sendMessage(@RequestBody Person person) {
        queueMessagingTemplate.convertAndSend(endPoint, person);
    }

    @Override
    @SqsListener(value = "DemoQueue", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void listen(Person person , Acknowledgment acknowledgment) {
        service.sendData(person)
            .doOnSuccess(aVoid -> handleSuccess(person, acknowledgment))
            .block();
    }

    private void handleSuccess(Person person, Acknowledgment acknowledgment) {
        acknowledgment.acknowledge();
        System.out.println("Person " +  person.getName() + "is processed.");
    }
}
