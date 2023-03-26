import dayjs from 'dayjs/esm';

import { IActivity, NewActivity } from './activity.model';

export const sampleWithRequiredData: IActivity = {
  id: 68021,
  title: 'Marketing Principal',
  publishedDate: dayjs('2023-03-26T05:49'),
};

export const sampleWithPartialData: IActivity = {
  id: 97944,
  title: 'Buckinghamshire Franc Product',
  destination: 'Future reciprocal platforms',
  publishedDate: dayjs('2023-03-26T09:02'),
};

export const sampleWithFullData: IActivity = {
  id: 1205,
  title: 'Sports encompassing extend',
  destination: 'Concrete',
  publishedDate: dayjs('2023-03-26T12:01'),
  bookedDate: dayjs('2023-03-26T19:59'),
};

export const sampleWithNewData: NewActivity = {
  title: 'invoice bandwidth',
  publishedDate: dayjs('2023-03-26T01:06'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
