package com.my.foody.domain.address.repo;

import com.my.foody.domain.address.entity.Address;
import com.my.foody.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserOrderByCreatedAtDesc(User user);
    Optional<Address> findByUserIdAndIsMain(Long userId, Boolean isMain);
}
