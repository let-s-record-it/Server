package com.sillim.recordit.support.security;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.AuthorizedUser;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class MockAuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Member member = createMember();

        AuthorizedUser authorizedUser = new AuthorizedUser(member, new HashMap<>(),
                member.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        authorizedUser, "password", authorizedUser.getAuthorities()));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private Member createMember() {
        return Member.builder()
                .auth(new Auth("1234567", OAuthProvider.KAKAO))
                .name("mock")
                .job("")
                .deleted(false)
                .memberRole(List.of(MemberRole.ROLE_USER))
                .build();
    }
}
