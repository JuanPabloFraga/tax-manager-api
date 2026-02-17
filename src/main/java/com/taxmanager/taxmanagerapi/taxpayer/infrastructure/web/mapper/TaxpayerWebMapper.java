package com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.mapper;

import com.taxmanager.taxmanagerapi.shared.exception.BadRequestException;
import com.taxmanager.taxmanagerapi.shared.fiscal.CuitValidator;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.CreateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerItemResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.UpdateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.CreateTaxpayerRequest;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.TaxpayerDetailResponse;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.TaxpayerListResponse;
import com.taxmanager.taxmanagerapi.taxpayer.infrastructure.web.dto.UpdateTaxpayerRequest;
import org.springframework.stereotype.Component;

@Component
public class TaxpayerWebMapper {

    public CreateTaxpayerCommand toCommand(CreateTaxpayerRequest request) {
        return new CreateTaxpayerCommand(
                request.businessName(),
                request.cuit(),
                parseTaxCondition(request.taxCondition()),
                request.fiscalAddress(),
                request.email(),
                request.phone()
        );
    }

    public UpdateTaxpayerCommand toCommand(UpdateTaxpayerRequest request) {
        return new UpdateTaxpayerCommand(
                request.businessName(),
                parseTaxCondition(request.taxCondition()),
                request.fiscalAddress(),
                request.email(),
                request.phone()
        );
    }

    public TaxpayerDetailResponse toResponse(TaxpayerDetailResult result) {
        return new TaxpayerDetailResponse(
                result.id(),
                result.businessName(),
                CuitValidator.format(result.cuit()),
                result.taxCondition().name(),
                result.fiscalAddress(),
                result.email(),
                result.phone(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    public TaxpayerListResponse toListResponse(TaxpayerItemResult result) {
        return new TaxpayerListResponse(
                result.id(),
                result.businessName(),
                CuitValidator.format(result.cuit()),
                result.taxCondition().name(),
                result.active()
        );
    }

    private TaxCondition parseTaxCondition(String value) {
        try {
            return TaxCondition.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Condición fiscal inválida: '%s'. Valores válidos: %s"
                            .formatted(value, java.util.Arrays.toString(TaxCondition.values()))
            );
        }
    }
}