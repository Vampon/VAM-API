// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addProductInfo POST /api/productInfo/add */
export async function addProductInfoUsingPost(
  body: API.ProductInfoAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/productInfo/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteProductInfo POST /api/productInfo/delete */
export async function deleteProductInfoUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getProductInfoById GET /api/productInfo/get */
export async function getProductInfoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProductInfoByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductInfo_>('/api/productInfo/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductInfoBySearchTextPage GET /api/productInfo/get/searchText */
export async function listProductInfoBySearchTextPageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listProductInfoBySearchTextPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductInfo_>('/api/productInfo/get/searchText', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductInfo GET /api/productInfo/list */
export async function listProductInfoUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listProductInfoUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseListProductInfo_>('/api/productInfo/list', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductInfoByPage GET /api/productInfo/list/page */
export async function listProductInfoByPageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listProductInfoByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageProductInfo_>('/api/productInfo/list/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** offlineProductInfo POST /api/productInfo/offline */
export async function offlineProductInfoUsingPost(
  body: API.IdRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/offline', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** onlineProductInfo POST /api/productInfo/online */
export async function onlineProductInfoUsingPost(
  body: API.IdRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/online', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateProductInfo POST /api/productInfo/update */
export async function updateProductInfoUsingPost(
  body: API.ProductInfoUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/productInfo/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
