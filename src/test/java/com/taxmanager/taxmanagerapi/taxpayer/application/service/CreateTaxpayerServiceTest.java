package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import com.taxmanager.taxmanagerapi.shared.exception.ConflictException;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.CreateTaxpayerCommand;
import com.taxmanager.taxmanagerapi.taxpayer.application.dto.TaxpayerDetailResult;
import com.taxmanager.taxmanagerapi.taxpayer.domain.entity.Taxpayer;
import com.taxmanager.taxmanagerapi.taxpayer.domain.enums.TaxCondition;
import com.taxmanager.taxmanagerapi.taxpayer.domain.repository.TaxpayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaxpayerServiceTest {

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @InjectMocks
    private CreateTaxpayerService service;

    private static final String VALID_CUIT_FORMATTED = "20-12345678-6";
    private static final String VALID_CUIT_RAW = "20123456786";

    @Test
    @DisplayName("should create taxpayer successfully")
    void createSuccessfully() {
        var command = new CreateTaxpayerCommand(
                "López S.R.L.", VALID_CUIT_FORMATTED,
                TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234", "test@test.com", null
        );

        when(taxpayerRepository.existsByCuitAndActiveTrue(VALID_CUIT_RAW)).thenReturn(false);
        when(taxpayerRepository.save(any(Taxpayer.class))).thenAnswer(inv -> inv.getArgument(0));

        TaxpayerDetailResult result = service.execute(command);

        assertNotNull(result.id());
        assertEquals("López S.R.L.", result.businessName());
        assertEquals(VALID_CUIT_RAW, result.cuit());
        assertEquals(TaxCondition.RESPONSABLE_INSCRIPTO, result.taxCondition());
        verify(taxpayerRepository).save(any(Taxpayer.class));
    }

    @Test
    @DisplayName("should throw ConflictException when CUIT already exists")
    void duplicateCuit() {
        var command = new CreateTaxpayerCommand(
                "López S.R.L.", VALID_CUIT_FORMATTED,
                TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234", null, null
        );

        when(taxpayerRepository.existsByCuitAndActiveTrue(VALID_CUIT_RAW)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.execute(command));
        verify(taxpayerRepository, never()).save(any());
    }
}