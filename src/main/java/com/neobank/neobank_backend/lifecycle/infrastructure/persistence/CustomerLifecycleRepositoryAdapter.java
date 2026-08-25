package com.neobank.neobank_backend.lifecycle.infrastructure.persistence;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerLifecycleRepositoryAdapter
        implements CustomerLifecycleRepository {

    private final JpaCustomerLifecycleRepository jpaRepository;

    public CustomerLifecycleRepositoryAdapter(
            JpaCustomerLifecycleRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustomerLifecycle save(CustomerLifecycle lifecycle) {
        return jpaRepository.save(lifecycle);
    }

    @Override
    public Optional<CustomerLifecycle> findLatestByCustomerId(
            Long customerId
    ) {
        return jpaRepository
                .findFirstByCustomer_IdOrderByEffectiveAtDescIdDesc(
                        customerId
                );
    }

    @Override
    public List<CustomerLifecycle> findAllByCustomerId(
            Long customerId
    ) {
        return jpaRepository
                .findAllByCustomer_IdOrderByEffectiveAtAscIdAsc(
                        customerId
                );
    }

    @Override
    public boolean existsByCustomerId(
            Long customerId
    ) {
        return jpaRepository.existsByCustomer_Id(customerId);
    }
}