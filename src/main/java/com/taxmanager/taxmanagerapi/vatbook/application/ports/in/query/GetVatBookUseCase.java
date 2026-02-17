package com.taxmanager.taxmanagerapi.vatbook.application.ports.in.query;

import java.time.YearMonth;

import com.taxmanager.taxmanagerapi.vatbook.application.dto.VatBookResult;

public interface GetVatBookUseCase {

    VatBookResult getPurchases(YearMonth period);

    VatBookResult getSales(YearMonth period);
}
