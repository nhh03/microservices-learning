package com.nhh203.productservice.service.implement;

import com.nhh203.productservice.dto.req.CategoryRequest;
import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.repository.CategoryRepository;
import com.nhh203.productservice.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

   private final CategoryRepository categoryRepository;


    @Override
    public Flux<List<CategoryResponse>> findAll() {
        return null;
    }

    @Override
    public Page<CategoryResponse> findAllCategory(int page, int size) {
        return null;
    }

    @Override
    public List<CategoryResponse> getAllCategories(Integer pageNo, Integer pageSize, String sortBy) {
        return null;
    }

    @Override
    public CategoryResponse findById(Integer categoryId) {
        return null;
    }

    @Override
    public Mono<CategoryResponse> save(CategoryRequest categoryRequest) {


        return null;
    }

    @Override
    public CategoryResponse update(CategoryRequest categoryDto) {
        return null;
    }

    @Override
    public CategoryResponse update(Integer categoryId, CategoryRequest categoryDto) {
        return null;
    }

    @Override
    public void deleteById(Integer categoryId) {

    }
}
