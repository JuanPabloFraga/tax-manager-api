package com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.UpdateTaxpayerCommand;

public interface UpdateTaxpayerUseCase {
    TaxpayerDetailResult execute(UUID id, UpdateTaxpayerCommand command);
}