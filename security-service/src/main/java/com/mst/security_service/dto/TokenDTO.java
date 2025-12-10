package com.mst.security_service.dto;

import java.util.Set;

public record TokenDTO (String token,
                       String tokenType,
                       long expiresIn,
                       String username,
                       Set<String> roles){ }
