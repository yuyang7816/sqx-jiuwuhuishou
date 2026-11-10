import { createPageApi } from '@/api/admin'
import { post, resolveBusinessResponse } from '@/api/http'

export const productCategoryApi = createPageApi('admin/product/category')
export const productBrandApi = createPageApi('admin/product/brand')
export const productModelApi = createPageApi('admin/product/model')
export const productSpecApi = createPageApi('admin/product/spec')

export function pageProductBrand(data) {
  return productBrandApi.page(data)
}

export function pageProductModel(data) {
  return productModelApi.page(data)
}

export function pageProductSpec(data) {
  return productSpecApi.page(data)
}

export function saveProductBrand(data) {
  return productBrandApi.save(data)
}

export function updateProductBrand(data) {
  return productBrandApi.update(data)
}

export function deleteProductBrand(data) {
  return productBrandApi.delete(data)
}

export function saveProductModel(data) {
  return productModelApi.save(data)
}

export function updateProductModel(data) {
  return productModelApi.update(data)
}

export function deleteProductModel(data) {
  return productModelApi.delete(data)
}

export function saveProductSpec(data) {
  return productSpecApi.save(data)
}

export function updateProductSpec(data) {
  return productSpecApi.update(data)
}

export function deleteProductSpec(data) {
  return productSpecApi.delete(data)
}

export function getProductBrandInfo(id) {
  return post('admin/product/brand/info', { id }).then(resolveBusinessResponse)
}

export function getProductModelInfo(id) {
  return post('admin/product/model/info', { id }).then(resolveBusinessResponse)
}

export function getProductSpecInfo(id) {
  return post('admin/product/spec/info', { id }).then(resolveBusinessResponse)
}

export function loadProductSpecTemplate(modelId) {
  return post('admin/product/spec/template', { modelId }).then(resolveBusinessResponse)
}
