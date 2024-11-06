package com.my.foody.domain.socialAccount.repo;

import com.my.foody.domain.socialAccount.entity.SocialAccount;
import com.my.foody.domain.user.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    boolean existsByProviderAndUserId(Provider provider, Long userId);

    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    Optional<SocialAccount> findByProviderAndProviderId(Provider provider, String providerId);
}
