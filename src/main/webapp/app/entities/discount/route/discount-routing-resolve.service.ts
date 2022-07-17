import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiscount, Discount } from '../discount.model';
import { DiscountService } from '../service/discount.service';

@Injectable({ providedIn: 'root' })
export class DiscountRoutingResolveService implements Resolve<IDiscount> {
  constructor(protected service: DiscountService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDiscount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((discount: HttpResponse<Discount>) => {
          if (discount.body) {
            return of(discount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Discount());
  }
}
