package com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Solicitud para crear un comprobante")
public record CreateVoucherRequest(

        @NotBlank
        @Schema(description = "Categoría: PURCHASE o SALE", example = "SALE")
        String category,

        @NotBlank
        @Schema(description = "Tipo de comprobante", example = "FACTURA_A")
        String voucherType,

        @NotBlank
        @Schema(description = "Fecha de emisión (YYYY-MM-DD)", example = "2026-02-15")
        String issueDate,

        @NotNull
        @Positive
        @Schema(description = "Punto de venta (1 a 99999)", example = "1")
        Integer pointOfSale,

        @NotNull
        @Positive
        @Schema(description = "Número de comprobante", example = "1542")
        Long voucherNumber,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Monto neto", example = "100000.0000")
        BigDecimal netAmount,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Monto IVA", example = "21000.0000")
        BigDecimal vatAmount,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Monto exento", example = "0.0000")
        BigDecimal exemptAmount,

        @NotNull
        @Positive
        @Schema(description = "Monto total", example = "121000.0000")
        BigDecimal totalAmount,

        @Schema(description = "Descripción (opcional)", example = "Servicios de consultoría febrero 2026")
        String description
) {}
