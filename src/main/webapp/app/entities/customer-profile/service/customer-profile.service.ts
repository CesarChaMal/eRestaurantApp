import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICustomerProfile, getCustomerProfileIdentifier } from '../customer-profile.model';

export type EntityResponseType = HttpResponse<ICustomerProfile>;
export type EntityArrayResponseType = HttpResponse<ICustomerProfile[]>;

@Injectable({ providedIn: 'root' })
export class CustomerProfileService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-profiles');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/customer-profiles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(customerProfile: ICustomerProfile): Observable<EntityResponseType> {
    return this.http.post<ICustomerProfile>(this.resourceUrl, customerProfile, { observe: 'response' });
  }

  update(customerProfile: ICustomerProfile): Observable<EntityResponseType> {
    return this.http.put<ICustomerProfile>(
      `${this.resourceUrl}/${getCustomerProfileIdentifier(customerProfile) as string}`,
      customerProfile,
      { observe: 'response' }
    );
  }

  partialUpdate(customerProfile: ICustomerProfile): Observable<EntityResponseType> {
    return this.http.patch<ICustomerProfile>(
      `${this.resourceUrl}/${getCustomerProfileIdentifier(customerProfile) as string}`,
      customerProfile,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICustomerProfile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomerProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomerProfile[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCustomerProfileToCollectionIfMissing(
    customerProfileCollection: ICustomerProfile[],
    ...customerProfilesToCheck: (ICustomerProfile | null | undefined)[]
  ): ICustomerProfile[] {
    const customerProfiles: ICustomerProfile[] = customerProfilesToCheck.filter(isPresent);
    if (customerProfiles.length > 0) {
      const customerProfileCollectionIdentifiers = customerProfileCollection.map(
        customerProfileItem => getCustomerProfileIdentifier(customerProfileItem)!
      );
      const customerProfilesToAdd = customerProfiles.filter(customerProfileItem => {
        const customerProfileIdentifier = getCustomerProfileIdentifier(customerProfileItem);
        if (customerProfileIdentifier == null || customerProfileCollectionIdentifiers.includes(customerProfileIdentifier)) {
          return false;
        }
        customerProfileCollectionIdentifiers.push(customerProfileIdentifier);
        return true;
      });
      return [...customerProfilesToAdd, ...customerProfileCollection];
    }
    return customerProfileCollection;
  }
}
