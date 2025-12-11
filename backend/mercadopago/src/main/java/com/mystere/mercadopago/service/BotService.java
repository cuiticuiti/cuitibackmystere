package com.mystere.mercadopago.service;

import com.mystere.mercadopago.model.Producto;
import com.mystere.mercadopago.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BotService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductoRepository productoRepository;

    public BotService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public String preguntar(String pregunta) {

        // === Listar productos en stock ===
        List<Producto> productosEnStock = productoRepository.findAll()
                .stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());

        // Texto con catálogo actual
        String catalogo = productosEnStock.stream()
                .map(p -> "- " + p.getNombre() + " ($" + p.getPrecio() + ") | Género: " + p.getGenero())
                .collect(Collectors.joining("\n"));

        // === Mensaje del sistema ===
        String reglas =
                "Sos el asistente oficial de Mystère Fragancias, una perfumería árabe de Mendoza. " +
                        "Hablás como un vendedor experto: cálido, amable, cercano y profesional. " +
                        "Tu objetivo es ayudar al cliente a encontrar su perfume ideal.\n\n" +

                        "REGLAS IMPORTANTES:\n" +
                        "1) SOLO podés recomendar perfumes que estén en este catálogo y tengan stock disponible.\n" +
                        "2) NO inventes perfumes ni recomendaciones.\n" +
                        "3) Si te piden algo que no existe, ofrecé alternativas reales del catálogo.\n" +
                        "4) Respondé siempre en pocas líneas, claro y profesional.\n\n" +

                        "FUNCIONES ESPECIALES:\n" +
                        "• Comparar perfumes del catálogo con fragancias famosas (Sauvage, Bleu de Chanel, Baccarat Rouge, etc.).\n" +
                        "• Hacer recomendaciones según PRESUPUESTO (económico, medio, premium).\n" +
                        "• Recomendar según ESTACIÓN del año:\n" +
                        "   - Verano → frescos, cítricos, marinos.\n" +
                        "   - Invierno → intensos, orientales, amaderados.\n" +
                        "   - Primavera → florales, dulces suaves.\n" +
                        "   - Otoño → cálidos, especiados.\n" +
                        "• Recomendar según MOMENTO DEL DÍA (día = fresco, noche = fuerte/elegante).\n" +
                        "• Estilo vendedor profesional: amable, cálido, útil. Podés cerrar con frases como:\n" +
                        "   “¿Querés que te cuente duración y notas? 😊”\n\n" +

                        "CATÁLOGO ACTUAL (solo productos en stock):\n"
                        + catalogo;


        // === Armado del request ===
        Map<String, Object> message1 = Map.of(
                "role", "system",
                "content", reglas
        );

        Map<String, Object> message2 = Map.of(
                "role", "user",
                "content", pregunta
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", List.of(message1, message2));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        // Request a OpenAI
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
        );

        // Extraer mensaje
        Map<String, Object> msg = (Map<String, Object>)
                ((List) response.getBody().get("choices")).get(0);

        return (String) ((Map) msg.get("message")).get("content");
    }
}
