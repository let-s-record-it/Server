package com.sillim.recordit.member.mapper;

import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.member.domain.Member;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class AuthorizedUserMapper {

	public AuthorizedUser toAuthorizedUser(Member member) {
		HashMap<String, Object> attributes = new HashMap<>();
		attributes.put("id", member.getId());
		return new AuthorizedUser(member, attributes, member.getAuthorities());
	}
}
