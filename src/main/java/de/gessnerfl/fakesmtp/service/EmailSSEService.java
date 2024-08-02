package de.gessnerfl.fakesmtp.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class EmailSSEService {

    private final Logger logger;

    @Autowired
    public EmailSSEService(Logger logger) {
        this.logger = logger;
    }

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        logger.info("new emitter registered");
        emitter.onCompletion(() -> {
            logger.info("emitter completed ... ");
            emitters.remove(emitter);
        });
        emitter.onError(throwable -> {
            logger.info("emitter error'ed: " + throwable.getMessage());
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            logger.info("emitter timed out");
            emitters.remove(emitter);
        });
    }

    public void sendEventFor(Long id, String rawData) {
        for (SseEmitter emitter : emitters) {
            logger.info("sending ...");
            try {
                // TODO what to sent?
                emitter.send(SseEmitter.event().id("NEW_EMAIL").data(new SSEResponse(id, "place here some raw data")).build(), MediaType.APPLICATION_JSON);
            }
            catch (IOException e) {
                logger.error("Could not send message", e);
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }

    public SseEmitter subscribe(Long timeout) {
        var newEmitter = new SseEmitter(timeout == null ? 0 : timeout);
        addEmitter(newEmitter);
        return newEmitter;
    }

    private record SSEResponse (Long id, String rawData) {

    }
}
