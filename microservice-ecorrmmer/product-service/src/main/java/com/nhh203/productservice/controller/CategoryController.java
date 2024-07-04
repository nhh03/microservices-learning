package com.nhh203.productservice.controller;


import com.nhh203.productservice.dto.req.CategoryRequest;
import com.nhh203.productservice.dto.res.CategoryResponse;
import com.nhh203.productservice.service.CategoryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // TODO : fetch all categories
    @GetMapping("")
    public ResponseEntity<Flux<CategoryResponse>> findAll() {
        log.info("CategoryDto List, controller; fetch all categories");
        return ResponseEntity.ok(categoryService.findAll());
    }


    // TODO : create category
    @PostMapping
    public ResponseEntity<Mono<CategoryResponse>> save(@RequestBody @NotNull(message = "Input must not be NULL")
                                                       @Valid final CategoryRequest categoryRequest) {
        log.info("CategoryDto, resource; save category");
        return ResponseEntity.ok(categoryService.save(categoryRequest));
    }


    // TODO: Get all list categories with paging
    @GetMapping("/paging")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryResponse> categoryPage = categoryService.findAllCategory(page, size);
        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }


    // TODO: Get all list categories with paging and sorting
    @GetMapping("/paging-and-sorting")
    public ResponseEntity<List<CategoryResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "categoryId") String sortBy) {

        List<CategoryResponse> list = categoryService.getAllCategories(pageNo, pageSize, sortBy);

        return new ResponseEntity<List<CategoryResponse>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    // TODO : fetch category by id
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable("categoryId")
                                                     @NotBlank(message = "Input must not be blank")
                                                     @Valid final String categoryId) {
        log.info("CategoryDto, resource; fetch category by id");
        return ResponseEntity.ok(categoryService.findById(Integer.parseInt(categoryId)));
    }

    // TODO: Update information of a category
    @PutMapping("/{categoryId}")
    @Transactional
    public ResponseEntity<CategoryResponse> update(@PathVariable("categoryId")
                                                   @NotBlank(message = "Input must not be blank")
                                                   @Valid final String categoryId,
                                                   @RequestBody @NotNull(message = "Input must not be NULL")
                                                   @Valid final CategoryRequest categoryRequest) {
        log.info("CategoryDto, resource; update category with categoryId");
        return ResponseEntity.ok(categoryService.update(Integer.parseInt(categoryId), categoryRequest));
    }

    // Delete a category
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
        log.info("Boolean, resource; delete category by id");
        categoryService.deleteById(Integer.parseInt(categoryId));
        return ResponseEntity.ok(true);
    }


}
