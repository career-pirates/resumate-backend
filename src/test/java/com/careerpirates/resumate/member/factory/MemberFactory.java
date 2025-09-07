package com.careerpirates.resumate.member.factory;

import com.careerpirates.resumate.member.domain.entity.Member;
import com.careerpirates.resumate.member.domain.enums.OAuthProvider;
import com.careerpirates.resumate.member.domain.enums.Role;

public class MemberFactory {

    public static Member createMember(String email) {
        return Member.builder()
                .provider(OAuthProvider.GOOGLE)
                .providerUserId("1")
                .email(email)
                .nickname("")
                .role(Role.USER)
                .build();
    }
}
