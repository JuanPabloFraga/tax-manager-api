package com.taxmanager.taxmanagerapi.voucher.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import com.taxmanager.taxmanagerapi.voucher.application.dto.CreateVoucherCommand;
import com.taxmanager.taxmanagerapi.voucher.application.dto.VoucherDetailResult;
import com.taxmanager.taxmanagerapi.voucher.domain.entity.Voucher;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherCategory;
import com.taxmanager.taxmanagerapi.voucher.domain.enums.VoucherType;
import com.taxmanager.taxmanagerapi.voucher.domain.repository.VoucherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @InjectMocks
    private CreateVoucherService service;

    @Test
    @DisplayName("Crea comprobante exitosamente cuando el contribuyente existe")
    void createSuccessfully() {
        // Arrange
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "30712345671", TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234, CABA", "contacto@lopez.com.ar", null
        );
        UUID taxpayerId = taxpayer.getId();

        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.of(taxpayer));
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateVoucherCommand command = new CreateVoucherCommand(
                VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 1542,
                new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                new BigDecimal("0.0000"), new BigDecimal("121000.0000"),
                "Consultoría"
        );

        // Act
        VoucherDetailResult result = service.execute(taxpayerId, command);

        // Assert
        assertNotNull(result.id());
        assertEquals(taxpayerId, result.taxpayerId());
        assertEquals("López S.R.L.", result.taxpayerBusinessName());
        assertEquals("30-71234567-1", result.taxpayerCuit());
        assertEquals(VoucherCategory.SALE, result.category());
        assertEquals(VoucherType.FACTURA_A, result.voucherType());
        assertEquals(0, new BigDecimal("121000.0000").compareTo(result.totalAmount()));

        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    @DisplayName("Falla si el contribuyente no existe")
    void failsWhenTaxpayerNotFound() {
        UUID taxpayerId = UUID.randomUUID();
        when(taxpayerRepository.findById(taxpayerId)).thenReturn(Optional.empty());

        CreateVoucherCommand command = new CreateVoucherCommand(
                VoucherCategory.SALE, VoucherType.FACTURA_A,
                LocalDate.of(2026, 2, 15), 1, 1542,
                new BigDecimal("100000.0000"), new BigDecimal("21000.0000"),
                new BigDecimal("0.0000"), new BigDecimal("121000.0000"),
                null
        );

        assertThrows(ResourceNotFoundException.class, () ->
                service.execute(taxpayerId, command));

        verify(voucherRepository, never()).save(any());
    }
}
