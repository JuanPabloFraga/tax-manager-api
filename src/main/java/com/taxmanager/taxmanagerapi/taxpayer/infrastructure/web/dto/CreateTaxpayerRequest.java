package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para crear un contribuyente")
public record CreateTaxpayerRequest(

        @NotBlank
        @Schema(description = "Razón social", example = "López S.R.L.")
        String businessName,

        @NotBlank
        @Schema(description = "CUIT con guiones", example = "30-71234567-1")
        String cuit,

        @NotNull
        @Schema(description = "Condición fiscal ante AFIP", example = "RESPONSABLE_INSCRIPTO")
        String taxCondition,

        @NotBlank
        @Schema(description = "Domicilio fiscal", example = "Av. Corrientes 1234, CABA")
        String fiscalAddress,

        @Schema(description = "Email de contacto (opcional)", example = "contacto@lopez.com.ar")
        String email,

        @Schema(description = "Teléfono (opcional)", example = "+54 11 4567-8900")
        String phone
) {}