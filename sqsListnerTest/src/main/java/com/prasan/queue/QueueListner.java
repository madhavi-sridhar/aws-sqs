package com.prasan.queue;

import com.prasan.dto.Person;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;

public interface QueueListner {
    void listen(Person person, Acknowledgment acknowledgment);
}
