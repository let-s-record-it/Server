package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;

public interface OidcClient {

	OidcPublicKeys getOidcPublicKeys();
}
