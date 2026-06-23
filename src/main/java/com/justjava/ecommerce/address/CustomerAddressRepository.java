package com.justjava.ecommerce.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

    List<CustomerAddress> findByCustomerIdOrderByIsDefaultDescCreatedAtAsc(UUID customerId);

    Optional<CustomerAddress> findByCustomerIdAndIsDefaultTrue(UUID customerId);

    long countByCustomerId(UUID customerId);
}
