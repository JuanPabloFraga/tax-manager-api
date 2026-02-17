package com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.controller;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import com.taxmanager.taxmanagerapi.shared.exception.BadRequestException;
import com.taxmanager.taxmanagerapi.vatbook.application.ports.in.query.GetVatBookUseCase;
import com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.dto.VatBookResponse;
import com.taxmanager.taxmanagerapi.vatbook.infrastructure.web.mapper.VatBookWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vat-books")
@Tag(name = "VAT Books", description = "Libros IVA Compras y Ventas")
public class VatBookController {

    private final GetVatBookUseCase getVatBookUseCase;
    private final VatBookWebMapper mapper;

    public VatBookController(GetVatBookUseCase getVatBookUseCase,
                             VatBookWebMapper mapper) {
        this.getVatBookUseCase = getVatBookUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/purchases")
    @Operation(summary = "Libro IVA Compras de un período")
    public ResponseEntity<VatBookResponse> getPurchases(@RequestParam String period) {
        YearMonth yearMonth = parsePeriod(period);
        var result = getVatBookUseCase.getPurchases(yearMonth);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/sales")
    @Operation(summary = "Libro IVA Ventas de un período")
    public ResponseEntity<VatBookResponse> getSales(@RequestParam String period) {
        YearMonth yearMonth = parsePeriod(period);
        var result = getVatBookUseCase.getSales(yearMonth);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    private YearMonth parsePeriod(String period) {
        try {
            return YearMonth.parse(period);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "Formato de período inválido: '" + period + "'. Formato esperado: YYYY-MM");
        }
    }
}
