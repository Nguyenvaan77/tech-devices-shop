package com.example.web.service.inter;

import com.example.web.dto.oauth2.OAuthAccountCreateDto;
import com.example.web.entity.OAuthAccount;
import com.example.web.util.AuthProvider;

public interface OAuthAccountService {
    OAuthAccount createOAuthAccount(OAuthAccountCreateDto dto);
    OAuthAccount getAuthAccountByProviderAndSub(AuthProvider provider, String sub);
    Boolean existOAuthAccountByProviderAndSub(String provider, String sub);
}
