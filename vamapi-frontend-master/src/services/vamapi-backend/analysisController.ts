// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

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
