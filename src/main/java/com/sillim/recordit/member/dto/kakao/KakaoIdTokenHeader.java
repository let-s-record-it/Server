package com.sillim.recordit.member.domain;

public record IdTokenHeader(String kid,
                            String typ,
                            String alg) {

}
