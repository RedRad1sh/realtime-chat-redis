package dev.nkucherenko.redischat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

/**
 * Thymeleaf doesn't work properly with reactive streams. Page is loading forever
 */
@ConditionalOnExpression("${mono.thymeleaf.enabled:false}")
@Controller
@Slf4j
public class ThymeleafController {
    @GetMapping("/chat")
    public String home(Model model) {
        WebClient client = WebClient.create("http://localhost:9092");
        Flux<String> stringFlux = client.get()
                .uri("/chatAlive")
                .retrieve()
                .bodyToFlux(String.class);
        model.addAttribute("messageList", new ReactiveDataDriverContextVariable(stringFlux, 1));
        return "index";
    }
}
