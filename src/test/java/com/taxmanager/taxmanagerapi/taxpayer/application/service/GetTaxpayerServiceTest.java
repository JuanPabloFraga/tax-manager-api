package com.taxmanager.taxmanagerapi.taxpayer.application.service;

import java.util.Optional;
import java.util.UUID;

import com.taxmanager.taxmanagerapi.shared.exception.ResourceNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTaxpayerServiceTest {

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @InjectMocks
    private GetTaxpayerService service;

    @Test
    @DisplayName("should find taxpayer by id")
    void findById() {
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "20123456786",
                TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234", null, null
        );

        when(taxpayerRepository.findById(taxpayer.getId())).thenReturn(Optional.of(taxpayer));

        var result = service.findById(taxpayer.getId());

        assertEquals(taxpayer.getId(), result.id());
        assertEquals("López S.R.L.", result.businessName());
    }

    @Test
    @DisplayName("should throw when taxpayer not found by id")
    void notFoundById() {
        UUID id = UUID.randomUUID();
        when(taxpayerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    @DisplayName("should find taxpayer by CUIT (formatted input)")
    void findByCuit() {
        Taxpayer taxpayer = Taxpayer.create(
                "López S.R.L.", "20123456786",
                TaxCondition.RESPONSABLE_INSCRIPTO,
                "Av. Corrientes 1234", null, null
        );

        when(taxpayerRepository.findByCuitAndActiveTrue("20123456786"))
                .thenReturn(Optional.of(taxpayer));

        var result = service.findByCuit("20-12345678-6");

        assertEquals("López S.R.L.", result.businessName());
    }

    @Test
    @DisplayName("should throw when taxpayer not found by CUIT")
    void notFoundByCuit() {
        when(taxpayerRepository.findByCuitAndActiveTrue("20123456786"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findByCuit("20-12345678-6"));
    }
}