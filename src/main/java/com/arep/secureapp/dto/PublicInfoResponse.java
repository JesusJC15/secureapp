package com.arep.secureapp.dto;

public record PublicInfoResponse(
        String application,
        String transport,
        String backend,
        String loginFlow) {
}
