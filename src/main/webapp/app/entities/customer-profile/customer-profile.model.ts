export interface ICustomerProfile {
  id?: string;
  name?: string;
  location?: string;
  locationRange?: string;
  referals?: string | null;
}

export class CustomerProfile implements ICustomerProfile {
  constructor(
    public id?: string,
    public name?: string,
    public location?: string,
    public locationRange?: string,
    public referals?: string | null
  ) {}
}

export function getCustomerProfileIdentifier(customerProfile: ICustomerProfile): string | undefined {
  return customerProfile.id;
}
