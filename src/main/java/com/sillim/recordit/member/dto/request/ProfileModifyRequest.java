package com.sillim.recordit.member.dto.request;

import jakarta.validation.constraints.Size;

public record ProfileModifyRequest(@Size(max = 10) String name, @Size(max = 20) String job) {
}
