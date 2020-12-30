package com.prasan.service;

import com.prasan.dto.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class MyService {
    @Autowired
    WebClient webClient;

    public Mono<Person> sendData(Person person) {
        return webClient.post()
                .uri("/process")
                .body(Mono.just(person), Person.class)
                .retrieve()
                .bodyToMono(Person.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .doAfterRetry(context -> System.out.println("retrying ...")));
    }
}
