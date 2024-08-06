package com.example.customer.controller;

import com.example.customer.service.UserAddressService;
import com.example.customer.viewmodel.address.ActiveAddressVm;
import com.example.customer.viewmodel.address.AddressDetailVm;
import com.example.customer.viewmodel.address.AddressPostVm;
import com.example.customer.viewmodel.user_address.UserAddressVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/storefront/user-address")
    public ResponseEntity<List<ActiveAddressVm>> getUserAddresses() {
        return ResponseEntity.ok(userAddressService.getUserAddressList());
    }

    @GetMapping("/storefront/user-address/default-address")
    public ResponseEntity<AddressDetailVm> getDefaultAddress() {
        return ResponseEntity.ok(userAddressService.getAddressDefault());
    }

    @PostMapping("/storefront/user-address")
    public ResponseEntity<UserAddressVm> createAddress(@Valid @RequestBody AddressPostVm addressPostVm) {
        return ResponseEntity.ok(userAddressService.createAddress(addressPostVm));
    }

    @DeleteMapping("/storefront/user-address/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/storefront/user-address/{id}")
    public ResponseEntity<Void> chooseDefaultAddress(@PathVariable Long id) {
        userAddressService.chooseDefaultAddress(id);
        return ResponseEntity.ok().build();
    }
}
