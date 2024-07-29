// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** getLatestChart GET /api/analysis/get/latest_chart */
export async function getLatestChartUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListBiResponse_>('/api/analysis/get/latest_chart', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getDataStatistics GET /api/analysis/get/statistics */
export async function getDataStatisticsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListBiResponse_>('/api/analysis/get/statistics', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getStatisticsInfo GET /api/analysis/interface/statistics_info */
export async function getStatisticsInfoUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserInterfaceInfoLog_>('/api/analysis/interface/statistics_info', {
    method: 'GET',
    ...(options || {}),
  });
}

/** listTopInvokeInterfaceInfo GET /api/analysis/top/interface/invoke */
export async function listTopInvokeInterfaceInfoUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListInterfaceInfoVO_>('/api/analysis/top/interface/invoke', {
    method: 'GET',
    ...(options || {}),
  });
}
