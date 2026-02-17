package com.taxmanager.taxmanagerapi.voucher.application.ports.in.command;

import java.util.UUID;

import com.taxmanager.taxmanagerapi.voucher.application.dto.CreateVoucherCommand;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;

public interface CreateVoucherUseCase {

    VoucherDetailResult execute(UUID taxpayerId, CreateVoucherCommand command);
}
