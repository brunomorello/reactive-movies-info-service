package com.bmo.reactivemoviesinfoservice.domain;

import java.util.List;

public record ErrorResponse(List<String> errors) {
}
