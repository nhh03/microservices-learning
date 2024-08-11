import { getVariationsByProductId } from '@catalogServices/ProductService';
import { useRouter } from 'next/router';
import React, { useEffect, useMemo, useState } from 'react';
import { UseFormGetValues, UseFormSetValue } from 'react-hook-form';
import Select, { SingleValue } from 'react-select';
import { toast } from 'react-toastify';

import { FormProduct } from '../models/FormProduct';
import { ProductOption } from '../models/ProductOption';
import { ProductVariation } from '../models/ProductVariation';
import { getProductOptions } from '../services/ProductOptionService';
import ProductVariant from './ProductVariant';

type Props = {
  getValue: UseFormGetValues<FormProduct>;
  setValue: UseFormSetValue<FormProduct>;
};

const ProductVariations = ({ getValue, setValue }: Props) => {
  const router = useRouter();
  const { id } = router.query;

  const [currentOption, setCurrentOption] = useState<SingleValue<ProductOption>>(null);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [selectedOptions, setSelectedOptions] = useState<string[]>([]);
  const [optionCombines, setOptionCombines] = useState<string[]>([]);

  useEffect(() => {
    if (id) {
      loadExistingVariant(id as string);
    }
  }, [id]);

  const loadExistingVariant = (id: string) => {
    getVariationsByProductId(id).then((results) => {
      if (results) {
        const listOptionCombine: string[] = [];
        const productVariants: ProductVariation[] = [];
        results.forEach((item) => {
          listOptionCombine.push(item.name || '');
          productVariants.push({
            id: item.id,
            optionName: item.name || '',
            optionGTin: item.gtin || '',
            optionSku: item.sku || '',
            optionPrice: item.price || 0,
            optionThumbnail: item.thumbnail,
            optionImages: item.productImages,
          });
        });
        setOptionCombines(listOptionCombine);
        setValue('productVariations', productVariants);
      }
    });
  };

  const options = useMemo(() => {
    return productOptions.map((option) => ({ value: option.name, label: option.name }));
  }, [productOptions]);

  let listVariant = useMemo(() => {
    return getValue('productVariations') || [];
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [optionCombines]);

  useEffect(() => {
    getProductOptions().then((data) => {
      setProductOptions(data);
    });
  }, []);

  const onAddOption = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    if (!currentOption) {
      toast.info('Select options first');
    } else {
      const index = selectedOptions.indexOf(currentOption.name);
      if (index === -1) {
        setSelectedOptions([...selectedOptions, currentOption.name]);
      } else {
        toast.info(`${currentOption.name} is selected. Select Other`);
      }
    }
  };

  const onDeleteOption = (event: React.MouseEvent<HTMLElement>, option: string) => {
    event.preventDefault();
    setOptionCombines([]);
    const result = selectedOptions.filter((_option) => _option !== option);
    setSelectedOptions([...result]);
  };

  const onGenerate = (event: React.MouseEvent<HTMLElement>) => {
    event.preventDefault();
    let productVar = getValue('productVariations') || [];
    const result = generateProductOptionCombinations();

    const listOptionCombine: string[] = [];
    const productVariants: ProductVariation[] = [];
    result.forEach((item) => {
      listOptionCombine.push(getValue('name')?.concat(' ', item) || '');
      productVariants.push({
        optionName: getValue('name')?.concat(' ', item) || '',
        optionGTin: getValue('gtin') || '',
        optionSku: getValue('sku') || '',
        optionPrice: getValue('price') || 0,
      });
    });
    setOptionCombines(listOptionCombine);
    setValue('productVariations', [...productVar, ...productVariants]);
  };

  const generateProductOptionCombinations = (): string[] => {
    const result: string[] = [];
    const productOp = getValue('productOptions') || [];

    selectedOptions.forEach((option) => {
      const combines = (document.getElementById(option) as HTMLInputElement).value
        .split(',')
        .map((item) => item.trim());

      const productOption = productOptions.find((productOption) => productOption.name === option);
      productOp.push({ productOptionId: productOption?.id, value: combines });
      setValue('productOptions', productOp);

      if (result.length === 0) {
        combines.forEach((item) => {
          result.push(item);
        });
      } else {
        let index = 0;
        const old = [...result];
        for (const oldItem of old) {
          for (const newItem of combines) {
            result[index] = oldItem.concat(' ', newItem);
            index++;
          }
        }
      }
    });

    return result;
  };

  const onDeleteVariation = (variant: ProductVariation) => {
    const result = optionCombines.filter((optionName) => optionName !== variant.optionName);
    setOptionCombines(result);

    let productVar = getValue('productVariations') || [];
    productVar = productVar.filter((item) => item.optionName !== variant.optionName);
    setValue('productVariations', productVar);
  };

  return (
    <>
      {/* Selection */}
      <div className="mb-3 d-flex justify-content-evenly">
        <label className="form-label m-0" htmlFor="option">
          Available Options
        </label>
        <Select
          className="w-50"
          options={options}
          isClearable
          isOptionDisabled={(option) => selectedOptions.includes(option.value.toString())}
          onChange={(option) => {
            if (option?.label) {
              setCurrentOption({
                id: +option.value,
                name: option.label,
              });
            }
          }}
        />
        <button className="btn btn-success" onClick={(event) => onAddOption(event)}>
          Add Option
        </button>
      </div>

      {/* Value options */}
      {selectedOptions.length > 0 && (
        <div className="mb-3">
          <h5 className="mb-3">Value Options</h5>
          <div className="mb-3">
            {(selectedOptions || []).map((option) => (
              <div className="mb-3 d-flex gap-4" key={option}>
                <label className="form-label flex-grow-1" htmlFor={option}>
                  {option}
                </label>
                <input type="text" id={option} className="form-control w-75" />
                <button
                  className="btn btn-danger"
                  onClick={(event) => onDeleteOption(event, option)}
                >
                  <i className="bi bi-x"></i>
                </button>
              </div>
            ))}
          </div>
          <div className="text-center">
            <button className="btn btn-info" onClick={onGenerate}>
              Generate Combine
            </button>
          </div>
        </div>
      )}

      {/* Product variations */}
      {listVariant.length > 0 && (
        <div className="mb-3">
          <h5 className="mb-3">Product Variations</h5>

          {listVariant.map((variant, index) => (
            <ProductVariant
              key={variant.optionName}
              index={index}
              variant={variant}
              onDelete={onDeleteVariation}
            />
          ))}
        </div>
      )}
    </>
  );
};

export default ProductVariations;
