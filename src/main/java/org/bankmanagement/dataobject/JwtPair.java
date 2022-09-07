package org.bankmanagement.dataobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPair {
    private final String accessToken;
    private final String refreshToken;
}
