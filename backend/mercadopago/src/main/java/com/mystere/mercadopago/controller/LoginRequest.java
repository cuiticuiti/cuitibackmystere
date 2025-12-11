package com.mystere.mercadopago.controller;

public record LoginRequest(
        String username,
        String password
) {}
