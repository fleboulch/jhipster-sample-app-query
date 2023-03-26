import dayjs from 'dayjs/esm';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
  title: 'Marketing Principal',
  publishedDate: dayjs('2023-03-26T05:41'),
};

export const sampleWithPartialData: IActivity = {
  id: 97944,
  title: 'Buckinghamshire Franc Product',
  destination: 'Future reciprocal platforms',
  publishedDate: dayjs('2023-03-26T08:54'),
};

export const sampleWithFullData: IActivity = {
  id: 1205,
  title: 'Sports encompassing extend',
  destination: 'Concrete',
  publishedDate: dayjs('2023-03-26T11:53'),
  bookedDate: dayjs('2023-03-26T19:51'),
};

export const sampleWithNewData: NewActivity = {
  title: 'invoice bandwidth',
  publishedDate: dayjs('2023-03-26T00:58'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
