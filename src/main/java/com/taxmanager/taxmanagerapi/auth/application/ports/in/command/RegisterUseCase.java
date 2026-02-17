package com.taxmanager.taxmanagerapi.auth.application.ports.in.command;

import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterCommand;
import com.taxmanager.taxmanagerapi.auth.application.dto.RegisterResult;

public interface RegisterUseCase {

    RegisterResult execute(RegisterCommand command);
}
