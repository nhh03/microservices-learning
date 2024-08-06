package com.example.customer.service;

import com.example.customer.exception.AccessDeniedException;
import com.example.customer.exception.NotFoundException;
import com.example.customer.model.UserAddress;
import com.example.customer.repository.UserAddressRepository;
import com.example.customer.utils.Constants;
import com.example.customer.viewmodel.address.ActiveAddressVm;
import com.example.customer.viewmodel.address.AddressDetailVm;
import com.example.customer.viewmodel.address.AddressVm;
import com.example.customer.viewmodel.address.AddressPostVm;
import com.example.customer.viewmodel.user_address.UserAddressVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAddressService {
	private final UserAddressRepository userAddressRepository;
	private final LocationService locationService;

	public UserAddressService(UserAddressRepository userAddressRepository, LocationService locationService) {
		this.userAddressRepository = userAddressRepository;
		this.locationService = locationService;
	}

	public List<ActiveAddressVm> getUserAddressList() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userId.equals("anonymousUser"))
			throw new AccessDeniedException(Constants.ERROR_CODE.UNAUTHENTICATED);

		List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
		List<AddressDetailVm> addressVmList = locationService.getAddressesByIdList(
				userAddressList.stream()
						.map(UserAddress::getAddressId)
						.collect(Collectors.toList()));

		List<ActiveAddressVm> addressActiveVms = userAddressList.stream()
				.flatMap(userAddress -> addressVmList.stream()
						.filter(addressDetailVm -> userAddress.getAddressId().equals(addressDetailVm.id()))
						.map(addressDetailVm -> new ActiveAddressVm(
								addressDetailVm.id(),
								addressDetailVm.contactName(),
								addressDetailVm.phone(),
								addressDetailVm.addressLine1(),
								addressDetailVm.city(),
								addressDetailVm.zipCode(),
								addressDetailVm.districtId(),
								addressDetailVm.districtName(),
								addressDetailVm.stateOrProvinceId(),
								addressDetailVm.stateOrProvinceName(),
								addressDetailVm.countryId(),
								addressDetailVm.countryName(),
								userAddress.getIsActive()
						))
				).toList();

		//sort by isActive
		Comparator<ActiveAddressVm> comparator = Comparator.comparing(ActiveAddressVm::isActive).reversed();
		return addressActiveVms.stream().sorted(comparator).collect(Collectors.toList());
	}

	public AddressDetailVm getAddressDefault() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userId.equals("anonymousUser"))
			throw new AccessDeniedException(Constants.ERROR_CODE.UNAUTHENTICATED);

		UserAddress userAddress = userAddressRepository.findByIsActiveTrue().orElseThrow(()
				-> new NotFoundException(Constants.ERROR_CODE.USER_ADDRESS_NOT_FOUND));

		AddressDetailVm addressVmList = locationService.getAddressById(userAddress.getAddressId());
		return addressVmList;
	}

	public UserAddressVm createAddress(AddressPostVm addressPostVm) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		AddressVm addressGetVm = locationService.createAddress(addressPostVm);
		UserAddress userAddress = UserAddress.builder()
				.userId(userId)
				.addressId(addressGetVm.id())
				.isActive(false)
				.build();

		return UserAddressVm.fromModel(userAddressRepository.save(userAddress), addressGetVm);

	}

	public void deleteAddress(Long id) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		UserAddress userAddress = userAddressRepository.findOneByUserIdAndAddressId(userId, id);
		if (userAddress == null) {
			throw new NotFoundException(Constants.ERROR_CODE.USER_ADDRESS_NOT_FOUND);
		}
		userAddressRepository.delete(userAddress);
	}

	public void chooseDefaultAddress(Long id) {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
		List<UserAddress> newUserAddressList = new ArrayList<>();
		for (UserAddress userAddress : userAddressList) {
			userAddress.setIsActive(Objects.equals(userAddress.getAddressId(), id));
			newUserAddressList.add(userAddress);
		}
		userAddressRepository.saveAll(newUserAddressList);
	}
}
