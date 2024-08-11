import { ProductAttributeValue } from '../models/ProductAttributeValue';
import { ProductAttributeValuePost } from '../models/ProductAttributeValuePost';

export async function getAttributeValueOfProduct(
  productId: string
): Promise<ProductAttributeValue[]> {
  const response = await fetch('/api/product/backoffice/product-attribute-value/' + productId);
  return response.json();
}

export async function createProductAttributeValueOfProduct(
  productAttributeValuePost: ProductAttributeValuePost
): Promise<ProductAttributeValuePost> {
  const response = await fetch('/api/product/backoffice/product-attribute-value', {
    method: 'POST',
    body: JSON.stringify(productAttributeValuePost),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return response.json();
}

export async function updateProductAttributeValueOfProduct(
  id: number,
  productAttributeValuePost: ProductAttributeValuePost
): Promise<number> {
  const res = await fetch('/api/product/backoffice/product-attribute-value/' + id, {
    method: 'PUT',
    body: JSON.stringify(productAttributeValuePost),
    headers: { 'Content-type': 'application/json; charset=UTF-8' },
  });
  return res.status;
}

export async function deleteProductAttributeValueOfProductById(id: number): Promise<number> {
  const res = await fetch('/api/product/backoffice/product-attribute-value/' + id, {
    method: 'DELETE',
  });
  return res.status;
}
