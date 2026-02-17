package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para actualizar un contribuyente")
public record UpdateTaxpayerRequest(

        @NotBlank
        @Schema(description = "Razón social", example = "López & Asociados S.R.L.")
        String businessName,

        @NotNull
        @Schema(description = "Condición fiscal ante AFIP", example = "RESPONSABLE_INSCRIPTO")
        String taxCondition,

        @NotBlank
        @Schema(description = "Domicilio fiscal", example = "Av. Rivadavia 5678, CABA")
        String fiscalAddress,

        @Schema(description = "Email de contacto (opcional)", example = "nuevo@lopez.com.ar")
        String email,

        @Schema(description = "Teléfono (opcional)", example = "+54 11 9876-5432")
        String phone
) {}