package dev.nkucherenko.redischat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.Rendering;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Controller
public class FreemarkerController {
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
