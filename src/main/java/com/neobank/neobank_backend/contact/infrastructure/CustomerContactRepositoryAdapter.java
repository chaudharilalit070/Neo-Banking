package com.neobank.neobank_backend.contact.infrastructure;

import com.neobank.neobank_backend.contact.domain.contact.ContactType;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContact;
import com.neobank.neobank_backend.contact.domain.contact.CustomerContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerContactRepositoryAdapter
        implements CustomerContactRepository {

    private final JpaCustomerContactRepository jpaCustomerContactRepository;

    @Override
    public CustomerContact save(CustomerContact customerContact) {
        return jpaCustomerContactRepository.save(customerContact);
    }

    @Override
    public Optional<CustomerContact> findById(Long id) {
        return jpaCustomerContactRepository.findById(id);
    }

    @Override
    public List<CustomerContact> findByCustomerId(UUID customerId) {
        return jpaCustomerContactRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<CustomerContact> findByCustomerIdAndContactTypeAndPrimaryTrue(
            UUID customerId,
            ContactType contactType
    ) {
        return jpaCustomerContactRepository
                .findByCustomerIdAndContactTypeAndPrimaryTrue(
                        customerId,
                        contactType
                );
    }

    @Override
    public boolean existsByCustomerIdAndContactTypeAndContactValue(
            UUID customerId,
            ContactType contactType,
            String contactValue
    ) {
        return jpaCustomerContactRepository
                .existsByCustomerIdAndContactTypeAndContactValue(
                        customerId,
                        contactType,
                        contactValue
                );
    }
}
