import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurantDiscount, RestaurantDiscount } from '../restaurant-discount.model';
import { RestaurantDiscountService } from '../service/restaurant-discount.service';

@Injectable({ providedIn: 'root' })
export class RestaurantDiscountRoutingResolveService implements Resolve<IRestaurantDiscount> {
  constructor(protected service: RestaurantDiscountService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurantDiscount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((restaurantDiscount: HttpResponse<RestaurantDiscount>) => {
          if (restaurantDiscount.body) {
            return of(restaurantDiscount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RestaurantDiscount());
  }
}
