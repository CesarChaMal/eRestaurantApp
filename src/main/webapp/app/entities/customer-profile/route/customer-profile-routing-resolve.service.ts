import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICustomerProfile, CustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

@Injectable({ providedIn: 'root' })
export class CustomerProfileRoutingResolveService implements Resolve<ICustomerProfile> {
  constructor(protected service: CustomerProfileService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICustomerProfile> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((customerProfile: HttpResponse<CustomerProfile>) => {
          if (customerProfile.body) {
            return of(customerProfile.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CustomerProfile());
  }
}
