package com.justjava.ecommerce.address;

import com.justjava.ecommerce.user.User;
import com.justjava.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerAddressService {

    public static final int MAX_ADDRESSES = 4;

    private final CustomerAddressRepository repo;
    private final UserRepository            userRepository;

    @Transactional(readOnly = true)
    public List<CustomerAddress> getAddresses(UUID customerId) {
        return repo.findByCustomerIdOrderByIsDefaultDescCreatedAtAsc(customerId);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerAddress> getDefaultAddress(UUID customerId) {
        return repo.findByCustomerIdAndIsDefaultTrue(customerId);
    }

    public CustomerAddress addAddress(UUID customerId, AddressForm form, boolean makeDefault) {
        long count = repo.countByCustomerId(customerId);
        if (count >= MAX_ADDRESSES) {
            throw new IllegalStateException("You can save a maximum of " + MAX_ADDRESSES + " addresses.");
        }
        boolean isDefault = makeDefault || count == 0;
        if (isDefault) clearDefault(customerId);
        User customer = userRepository.getReferenceById(customerId);
        return repo.save(CustomerAddress.builder()
                .customer(customer)
                .recipientName(form.getRecipientName())
                .phone(form.getPhone())
                .addressLine(form.getAddressLine())
                .city(form.getCity())
                .state(form.getState())
                .isDefault(isDefault)
                .build());
    }

    public void updateAddress(UUID customerId, UUID addressId, AddressForm form) {
        CustomerAddress addr = getOwned(customerId, addressId);
        addr.setRecipientName(form.getRecipientName());
        addr.setPhone(form.getPhone());
        addr.setAddressLine(form.getAddressLine());
        addr.setCity(form.getCity());
        addr.setState(form.getState());
    }

    public void setDefault(UUID customerId, UUID addressId) {
        getOwned(customerId, addressId);
        clearDefault(customerId);
        repo.findById(addressId).ifPresent(a -> a.setDefault(true));
    }

    public void delete(UUID customerId, UUID addressId) {
        CustomerAddress addr = getOwned(customerId, addressId);
        boolean wasDefault = addr.isDefault();
        repo.delete(addr);
        repo.flush();
        if (wasDefault) {
            repo.findByCustomerIdOrderByIsDefaultDescCreatedAtAsc(customerId)
                    .stream().findFirst().ifPresent(a -> a.setDefault(true));
        }
    }

    /** Called automatically after first successful payment — saves that order's shipping details as the default. */
    public void autoSaveFromCheckout(UUID customerId, String recipientName, String phone,
                                     String addressLine, String city, String state) {
        if (repo.countByCustomerId(customerId) > 0) return;
        User customer = userRepository.getReferenceById(customerId);
        repo.save(CustomerAddress.builder()
                .customer(customer)
                .recipientName(recipientName)
                .phone(phone)
                .addressLine(addressLine)
                .city(city)
                .state(state)
                .isDefault(true)
                .build());
    }

    private void clearDefault(UUID customerId) {
        repo.findByCustomerIdAndIsDefaultTrue(customerId).ifPresent(a -> a.setDefault(false));
    }

    private CustomerAddress getOwned(UUID customerId, UUID addressId) {
        CustomerAddress addr = repo.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        if (!addr.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Address does not belong to this customer");
        }
        return addr;
    }
}
