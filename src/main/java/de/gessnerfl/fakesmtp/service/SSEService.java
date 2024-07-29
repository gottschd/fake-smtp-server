package de.gessnerfl.fakesmtp.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SSEService {

    private final Logger logger;

    @Autowired
    public SSEService(Logger logger) {
        this.logger = logger;
    }

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        logger.info("new emitter registered");
        emitter.onCompletion(() -> {
            logger.info("emitter completted ... ");
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            logger.info("emitter timed out");
            emitters.remove(emitter);
        } );
    }

    @Scheduled(fixedRate = 1000)
    public void sendEvents() {
        for (SseEmitter emitter : emitters) {
            logger.info("sending ...");
            try {
                emitter.send(SseEmitter.event().data(System.currentTimeMillis()).build());
            } catch (IOException e) {
                logger.error("Could not send message", e);
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}
