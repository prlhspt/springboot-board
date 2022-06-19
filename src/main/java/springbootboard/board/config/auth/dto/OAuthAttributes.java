package springbootboard.board.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import springbootboard.board.domain.member.LoginType;
import springbootboard.board.domain.member.Member;
import springbootboard.board.domain.member.Role;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String email;
    private LoginType serviceType;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email,
                           LoginType serviceType) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.serviceType = serviceType;
    }


    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes) {

        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .username((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .serviceType(LoginType.GOOGLE)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .username((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .serviceType(LoginType.NAVER)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .username(email)
                .email(email)
                .role(Role.USER)
                .nickname(email)
                .loginType(serviceType)
                .build();
    }
}