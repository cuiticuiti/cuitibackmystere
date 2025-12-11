package com.mystere.mercadopago.controller;

import com.mystere.mercadopago.service.BotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bot")
@CrossOrigin("*")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping("/consultar")
    public Map<String, String> consultar(@RequestBody Map<String, String> body) {
        String pregunta = body.get("pregunta");
        String respuesta = botService.preguntar(pregunta);
        return Map.of("respuesta", respuesta);
    }
}
