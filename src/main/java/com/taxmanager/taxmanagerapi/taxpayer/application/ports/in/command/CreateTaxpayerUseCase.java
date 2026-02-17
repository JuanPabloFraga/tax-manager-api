package com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command;

import com.taxmanager.taxmanagerapi.taxpayer.application.dto.CreateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;

public interface CreateTaxpayerUseCase {
    TaxpayerDetailResult execute(CreateTaxpayerCommand command);
}