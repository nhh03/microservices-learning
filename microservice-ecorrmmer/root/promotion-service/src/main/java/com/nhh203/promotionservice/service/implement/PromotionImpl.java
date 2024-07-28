package com.nhh203.promotionservice.service.implement;

import com.nhh203.promotionservice.model.DiscountAppEntity;
import com.nhh203.promotionservice.model.DiscountCodeEntity;
import com.nhh203.promotionservice.model.dto.PromotionDTO;
import com.nhh203.promotionservice.repository.DiscountAppRepository;
import com.nhh203.promotionservice.repository.DiscountCodeRepository;
import com.nhh203.promotionservice.service.IDiscount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class PromotionImpl implements IDiscount {

//	@PersistenceContext
	////	private EntityManager entityManager;

	private final DiscountCodeRepository discountCodeRepository;
	private final DiscountAppRepository discountAppRepository;


	@Transactional
	@Override
	public List<DiscountAppEntity> addDiscount(PromotionDTO promotionDTO) {
		try {
			DiscountCodeEntity discountCodeEntity = DiscountCodeEntity
					.builder()
					.name(promotionDTO.getName())
					.description(promotionDTO.getDescription())
					.code(promotionDTO.getCode())
					.isActive(promotionDTO.getIsActive())
					.startDate(promotionDTO.getStartDate())
					.endDate(promotionDTO.getEndDate())
					.discountValue(promotionDTO.getDiscountValue())
					.idUser(promotionDTO.getIdUser()).build();

			List<DiscountAppEntity> discountAppEntities = promotionDTO
					.getIdProducts()
					.stream()
					.map(productId -> DiscountAppEntity
							.builder()
							.discountCode(discountCodeEntity)
							.idProduct(productId)
							.build())
					.toList();

			discountCodeEntity.setDiscountAppEntities(discountAppEntities);
			// entityManager.persist(discountCodeEntity);
			discountCodeRepository.save(discountCodeEntity);
			return discountAppEntities;
		} catch (Exception e) {
			log.error("Error: ", e);
			return null;
		}
	}

	@Override
	public List<DiscountAppEntity> findByProductId(String idProduct) {
		List<DiscountAppEntity> discountAppEntities = new ArrayList<DiscountAppEntity>();
		ZonedDateTime currentTime = ZonedDateTime.now();
		try {
			discountAppEntities = discountAppRepository.findDiscountAppsByProductIdWithinDateRange(idProduct, currentTime);
			return discountAppEntities;
		} catch (Exception e) {
			log.error("Error: ", e);
			return discountAppEntities;
		}
	}

	@Override
	public DiscountCodeEntity findById(Long id) {
		return discountCodeRepository.findById(id).orElse(null);
	}

	@Override
	public List<DiscountAppEntity> findByProductIdBetweenDate(String idProduct) {
		List<DiscountAppEntity> discountAppEntities = new ArrayList<DiscountAppEntity>();
		ZonedDateTime currentTime = ZonedDateTime.now();
		try {
			discountAppEntities = discountAppRepository.findDiscountAppsByProductIdWithinDateRange(idProduct, currentTime);
			return discountAppEntities;
		} catch (Exception e) {
			log.error("Error: ", e);
			return discountAppEntities;
		}
	}

	@Override
	public void updateDiscountCodeStatus(Long id, boolean isActive) {
		this.discountCodeRepository.updateDiscountCodeStatusById(id, isActive);
	}

	@Override
	public List<DiscountCodeEntity> findDiscountCodesByUserId(Long idUser) {
		List<DiscountCodeEntity> discountCodeEntities = new ArrayList<DiscountCodeEntity>();
		try {
			discountCodeEntities = this.discountCodeRepository.findByIdUser(idUser);
			return discountCodeEntities;
		} catch (Exception e) {
			log.error("Error: ", e);
			return discountCodeEntities;
		}
	}


	@Transactional
	@Override
	public List<DiscountAppEntity> updateDiscount(PromotionDTO promotionDTO) {
		try {
			Optional<DiscountCodeEntity> optionalDiscountCode = discountCodeRepository.findById(promotionDTO.getId());
			if (optionalDiscountCode.isPresent()) {
				DiscountCodeEntity discountCodeEntity = optionalDiscountCode.get();
				discountCodeEntity.setId(promotionDTO.getId());
				discountCodeEntity.setName(promotionDTO.getName());
				discountCodeEntity.setDescription(promotionDTO.getDescription());
				discountCodeEntity.setCode(promotionDTO.getCode());
				discountCodeEntity.setIsActive(promotionDTO.getIsActive());
				discountCodeEntity.setStartDate(promotionDTO.getStartDate());
				discountCodeEntity.setEndDate(promotionDTO.getEndDate());
				discountCodeEntity.setDiscountValue(promotionDTO.getDiscountValue());
				discountCodeEntity.setIdUser(promotionDTO.getIdUser());

				List<DiscountAppEntity> discountAppEntities = discountCodeEntity.getDiscountAppEntities();
				this.discountAppRepository.deleteAll(discountAppEntities);
				List<DiscountAppEntity> newDiscountAppEntities = promotionDTO.getIdProducts().stream().map(productId -> DiscountAppEntity
						.builder()
						.discountCode(discountCodeEntity)
						.idProduct(productId)
						.build()).toList();
				List<DiscountAppEntity> savedDiscountAppEntities = discountAppRepository.saveAll(newDiscountAppEntities);
				discountCodeEntity.setDiscountAppEntities(savedDiscountAppEntities);
				discountCodeRepository.save(discountCodeEntity);
				return savedDiscountAppEntities;
			} else {
				throw new ResourceNotFoundException("Discount code not found");
			}
		} catch (Exception e) {
			log.error("Error: ", e);
			return null;
		}

	}

	@Transactional
	@Override
	public boolean deleteByIdProductIn(List<String> idProducts) {
		long countBeforeDeletion = discountAppRepository.countByIdProductIn(idProducts);
		discountAppRepository.deleteByIdProductIn(idProducts);
		long countAfterDeletion = discountAppRepository.countByIdProductIn(idProducts);
		return countAfterDeletion < countBeforeDeletion;
	}

	@Override
	public List<DiscountCodeEntity> getActiveDiscountCodesBetweenDates(ZonedDateTime startDate, ZonedDateTime endDate) {
		List<DiscountCodeEntity> discountAppEntities = new ArrayList<DiscountCodeEntity>();
		try {
			discountAppEntities = discountCodeRepository.findActiveDiscountCodesBetweenDates(startDate, endDate);
			return discountAppEntities;
		} catch (Exception e) {
			log.error("Error: ", e);
			return new ArrayList<>();
		}
	}
}
