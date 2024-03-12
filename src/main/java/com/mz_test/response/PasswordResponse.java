package com.mz_test.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResponse {
    private String salt;
    private String password;

    @Builder
    public PasswordResponse(String salt, String password) {
        this.salt = salt;
        this.password = password;
    }
}
