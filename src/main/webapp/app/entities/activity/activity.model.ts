import dayjs from 'dayjs/esm';

export interface IActivity {
  id: number;
  title?: string | null;
  destination?: string | null;
  publishedDate?: dayjs.Dayjs | null;
  bookedDate?: dayjs.Dayjs | null;
}

export type NewActivity = Omit<IActivity, 'id'> & { id: null };
