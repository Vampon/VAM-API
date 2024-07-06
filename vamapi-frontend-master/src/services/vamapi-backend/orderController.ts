// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** closedProductOrder POST /api/order/closed */
export async function closedProductOrderUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.closedProductOrderUsingPOSTParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/order/closed', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** createOrder POST /api/order/create */
export async function createOrderUsingPost(
  body: API.PayCreateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductOrderVo_>('/api/order/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteProductOrder POST /api/order/delete */
export async function deleteProductOrderUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteProductOrderUsingPOSTParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/order/delete', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getProductOrderById GET /api/order/get */
export async function getProductOrderByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getProductOrderByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseProductOrderVo_>('/api/order/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listProductOrderByPage GET /api/order/list/page */
export async function listProductOrderByPageUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listProductOrderByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseOrderVo_>('/api/order/list/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** parseOrderNotifyResult POST /api/order/notify/order */
export async function parseOrderNotifyResultUsingPost(
  body: string,
  options?: { [key: string]: any },
) {
  return request<string>('/api/order/notify/order', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** queryOrderStatus POST /api/order/query/status */
export async function queryOrderStatusUsingPost(
  body: API.ProductOrderQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/order/query/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
