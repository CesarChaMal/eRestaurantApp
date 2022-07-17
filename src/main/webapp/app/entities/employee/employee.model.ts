import { IRestaurant } from 'app/entities/restaurant/restaurant.model';

export interface IEmployee {
  id?: string;
  description?: string | null;
  restaurant?: IRestaurant | null;
}

export class Employee implements IEmployee {
  constructor(public id?: string, public description?: string | null, public restaurant?: IRestaurant | null) {}
}

export function getEmployeeIdentifier(employee: IEmployee): string | undefined {
  return employee.id;
}
