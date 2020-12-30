package com.prasan.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasan.dto.Person;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.support.AcknowledgmentHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Arrays;

@Configuration
public class SqsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;
    @Value("${client.end-point.url}")
    private String clientUrl;

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(){
        return new QueueMessagingTemplate(amazonSQSAsync());
    }

    @Primary
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .build();
    }

//    @Bean
//    public QueueMessageHandlerFactory queueMessageHandlerFactory(ObjectMapper objectMapper) {
//        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();
//        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
//        messageConverter.setObjectMapper(objectMapper);
//        messageConverter.setSerializedPayloadClass(String.class);
//        messageConverter.setStrictContentTypeMatch(false);
//
//        PayloadArgumentResolver payloadArgumentResolver = new PayloadArgumentResolver(messageConverter);
//
//        AcknowledgmentHandlerMethodArgumentResolver acknowledgementResolver =
//                new AcknowledgmentHandlerMethodArgumentResolver("Acknowledgement");
//
//        factory.setArgumentResolvers(Arrays.asList(acknowledgementResolver, payloadArgumentResolver));
//        return factory;
//    }

    @Bean
    public WebClient getWebClient(){
        return WebClient.builder()
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(16*1024*1024))
            .build())
        .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()))
        .baseUrl(clientUrl)
        .build();
    }

}
