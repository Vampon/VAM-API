// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** doDailyCheckIn POST /api/dailyCheckIn/doCheckIn */
export async function doDailyCheckInUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/dailyCheckIn/doCheckIn', {
    method: 'POST',
    ...(options || {}),
  });
}
