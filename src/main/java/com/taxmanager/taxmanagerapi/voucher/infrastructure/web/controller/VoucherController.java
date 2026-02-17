package com.taxmanager.taxmanagerapi.voucher.infrastructure.web.controller;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.application.ports.in.command.CreateVoucherUseCase;
import com.taxmanager.taxmanagerapi.voucher.application.ports.in.query.GetVoucherUseCase;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.CreateVoucherRequest;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.VoucherDetailResponse;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.VoucherListResponse;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.mapper.VoucherWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Vouchers", description = "Gestión de comprobantes")
public class VoucherController {

    private final CreateVoucherUseCase createVoucherUseCase;
    private final GetVoucherUseCase getVoucherUseCase;
    private final VoucherWebMapper mapper;

    public VoucherController(CreateVoucherUseCase createVoucherUseCase,
                             GetVoucherUseCase getVoucherUseCase,
                             VoucherWebMapper mapper) {
        this.createVoucherUseCase = createVoucherUseCase;
        this.getVoucherUseCase = getVoucherUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/taxpayers/{taxpayerId}/vouchers")
    @Operation(summary = "Crear comprobante para un contribuyente")
    public ResponseEntity<VoucherDetailResponse> create(
            @PathVariable UUID taxpayerId,
            @Valid @RequestBody CreateVoucherRequest request) {
        var command = mapper.toCommand(request);
        var result = createVoucherUseCase.execute(taxpayerId, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result));
    }

    @GetMapping("/taxpayers/{taxpayerId}/vouchers")
    @Operation(summary = "Listar comprobantes de un contribuyente")
    public ResponseEntity<Page<VoucherListResponse>> findAllByTaxpayerId(
            @PathVariable UUID taxpayerId,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        VoucherCategory cat = category != null ? VoucherCategory.valueOf(category) : null;
        var page = getVoucherUseCase.findAllByTaxpayerId(taxpayerId, cat, pageable)
                .map(mapper::toListResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/vouchers/{id}")
    @Operation(summary = "Detalle de un comprobante")
    public ResponseEntity<VoucherDetailResponse> findById(@PathVariable UUID id) {
        var result = getVoucherUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(result));
    }
}
