package com.mystere.mercadopago.controller;

import java.util.List;

public record PaymentRequest(
        List<ItemRequest> items,
        String codigoDescuento
) {}
