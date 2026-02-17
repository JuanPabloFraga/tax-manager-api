package com.taxmanager.taxmanagerapi.auth.application.ports.in.command;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;

public interface RefreshTokenUseCase {

    AuthTokenResult execute(String refreshToken);
}
