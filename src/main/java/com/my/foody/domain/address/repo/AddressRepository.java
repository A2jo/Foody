package com.my.foody.domain.address.repo;

import com.my.foody.domain.address.entity.Address;

import java.util.List;
import java.util.Optional;

import com.my.foody.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUserId(Long userId);
    List<Address> findAllByUserOrderByCreatedAtDesc(User user);
}
