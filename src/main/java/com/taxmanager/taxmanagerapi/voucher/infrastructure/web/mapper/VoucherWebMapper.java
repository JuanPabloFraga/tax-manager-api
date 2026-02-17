package com.taxmanager.taxmanagerapi.voucher.infrastructure.web.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.taxmanager.taxmanagerapi.shared.exception.BadRequestException;
import com.taxmanager.taxmanagerapi.voucher.application.dto.CreateVoucherCommand;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherItemResult;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.CreateVoucherRequest;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.VoucherDetailResponse;
import com.taxmanager.taxmanagerapi.voucher.infrastructure.web.dto.VoucherListResponse;
import org.springframework.stereotype.Component;

@Component
public class VoucherWebMapper {

    public CreateVoucherCommand toCommand(CreateVoucherRequest request) {
        return new CreateVoucherCommand(
                parseCategory(request.category()),
                parseVoucherType(request.voucherType()),
                parseIssueDate(request.issueDate()),
                request.pointOfSale(),
                request.voucherNumber(),
                request.netAmount(),
                request.vatAmount(),
                request.exemptAmount(),
                request.totalAmount(),
                request.description()
        );
    }

    public VoucherDetailResponse toResponse(VoucherDetailResult result) {
        return new VoucherDetailResponse(
                result.id(),
                result.taxpayerId(),
                result.taxpayerBusinessName(),
                result.taxpayerCuit(),
                result.category().name(),
                result.voucherType().name(),
                result.issueDate(),
                result.pointOfSale(),
                result.voucherNumber(),
                result.netAmount(),
                result.vatAmount(),
                result.exemptAmount(),
                result.totalAmount(),
                result.description(),
                result.createdAt()
        );
    }

    public VoucherListResponse toListResponse(VoucherItemResult result) {
        return new VoucherListResponse(
                result.id(),
                result.category().name(),
                result.voucherType().name(),
                result.issueDate(),
                result.pointOfSale(),
                result.voucherNumber(),
                result.totalAmount(),
                result.description()
        );
    }

    // ── Parse helpers ────────────────────────────────────────────────────

    private VoucherCategory parseCategory(String value) {
        try {
            return VoucherCategory.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Categoría inválida: '" + value + "'. Valores permitidos: PURCHASE, SALE");
        }
    }

    private VoucherType parseVoucherType(String value) {
        try {
            return VoucherType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Tipo de comprobante inválido: '" + value + "'. Valores permitidos: " +
                    "FACTURA_A, FACTURA_B, FACTURA_C, NOTA_CREDITO_A, NOTA_CREDITO_B, " +
                    "NOTA_CREDITO_C, NOTA_DEBITO_A, NOTA_DEBITO_B, NOTA_DEBITO_C, RECIBO, TICKET");
        }
    }

    private LocalDate parseIssueDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(
                    "Fecha de emisión inválida: '" + value + "'. Formato esperado: YYYY-MM-DD");
        }
    }
}
