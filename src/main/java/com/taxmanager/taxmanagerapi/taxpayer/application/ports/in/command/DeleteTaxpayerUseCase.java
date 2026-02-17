package com.taxmanager.taxmanagerapi.taxpayer.application.ports.in.command;

import java.util.UUID;

public interface DeleteTaxpayerUseCase {
    void execute(UUID id);
}