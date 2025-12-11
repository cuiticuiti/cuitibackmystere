package com.mystere.mercadopago.controller;

public record ItemRequest(
        String title,
        Integer quantity,
        Integer price // ← agregar este si no estaba
) {}

