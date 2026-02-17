package com.taxmanager.taxmanagerapi.auth.application.ports.in.command;

public interface LogoutUseCase {

    void execute(String refreshToken);
}
