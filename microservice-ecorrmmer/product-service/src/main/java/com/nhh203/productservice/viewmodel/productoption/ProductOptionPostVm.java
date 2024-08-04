package com.nhh203.productservice.viewmodel.productoption;

import jakarta.validation.constraints.NotBlank;

public record ProductOptionPostVm(@NotBlank String name) {
}
