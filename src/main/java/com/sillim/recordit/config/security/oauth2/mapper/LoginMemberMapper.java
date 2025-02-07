package com.sillim.recordit.config.security.oauth2.mapper;

import com.sillim.recordit.config.security.oauth2.LoginMember;
import com.sillim.recordit.member.domain.Member;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LoginMemberMapper {

	public LoginMember toLoginMember(Member member) {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", member.getId());
		return new LoginMember(member, attributes, member.getAuthorities());
	}
}
