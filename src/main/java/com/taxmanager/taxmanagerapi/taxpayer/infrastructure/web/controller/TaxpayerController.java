package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.controller;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.CreateTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.DeleteTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command.UpdateTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.query.GetTaxpayerUseCase;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.CreateTaxpayerRequest;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.TaxpayerDetailResponse;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.TaxpayerListResponse;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.UpdateTaxpayerRequest;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.mapper.TaxpayerWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/taxpayers")
@Tag(name = "Taxpayers", description = "Gestión de contribuyentes")
public class TaxpayerController {

    private final CreateTaxpayerUseCase createTaxpayerUseCase;
    private final UpdateTaxpayerUseCase updateTaxpayerUseCase;
    private final DeleteTaxpayerUseCase deleteTaxpayerUseCase;
    private final GetTaxpayerUseCase getTaxpayerUseCase;
    private final TaxpayerWebMapper mapper;

    public TaxpayerController(CreateTaxpayerUseCase createTaxpayerUseCase,
                              UpdateTaxpayerUseCase updateTaxpayerUseCase,
                              DeleteTaxpayerUseCase deleteTaxpayerUseCase,
                              GetTaxpayerUseCase getTaxpayerUseCase,
                              TaxpayerWebMapper mapper) {
        this.createTaxpayerUseCase = createTaxpayerUseCase;
        this.updateTaxpayerUseCase = updateTaxpayerUseCase;
        this.deleteTaxpayerUseCase = deleteTaxpayerUseCase;
        this.getTaxpayerUseCase = getTaxpayerUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Crear contribuyente")
    public ResponseEntity<TaxpayerDetailResponse> create(@Valid @RequestBody CreateTaxpayerRequest request) {
        var command = mapper.toCommand(request);
        var result = createTaxpayerUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @GetMapping
    @Operation(summary = "Listar contribuyentes activos")
    public ResponseEntity<Page<TaxpayerListResponse>> findAll(Pageable pageable) {
        var page = getTaxpayerUseCase.findAll(pageable)
                .map(mapper::toListResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de contribuyente")
    public ResponseEntity<TaxpayerDetailResponse> findById(@PathVariable UUID id) {
        var result = getTaxpayerUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar contribuyente por CUIT")
    public ResponseEntity<TaxpayerDetailResponse> findByCuit(@RequestParam String cuit) {
        var result = getTaxpayerUseCase.findByCuit(cuit);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar contribuyente")
    public ResponseEntity<TaxpayerDetailResponse> update(@PathVariable UUID id,
                                                         @Valid @RequestBody UpdateTaxpayerRequest request) {
        var command = mapper.toCommand(request);
        var result = updateTaxpayerUseCase.execute(id, command);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar contribuyente (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteTaxpayerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}