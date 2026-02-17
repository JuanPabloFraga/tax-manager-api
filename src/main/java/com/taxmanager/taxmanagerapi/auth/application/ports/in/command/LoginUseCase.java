package com.taxmanager.taxmanagerapi.auth.application.ports.in.command;

import com.taxmanager.taxmanagerapi.auth.application.dto.AuthTokenResult;
import com.taxmanager.taxmanagerapi.auth.application.dto.LoginCommand;

public interface LoginUseCase {

    AuthTokenResult execute(LoginCommand command);
}
