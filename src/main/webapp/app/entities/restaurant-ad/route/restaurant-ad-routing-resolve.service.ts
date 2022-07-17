import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurantAd, RestaurantAd } from '../restaurant-ad.model';
import { RestaurantAdService } from '../service/restaurant-ad.service';

@Injectable({ providedIn: 'root' })
export class RestaurantAdRoutingResolveService implements Resolve<IRestaurantAd> {
  constructor(protected service: RestaurantAdService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurantAd> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((restaurantAd: HttpResponse<RestaurantAd>) => {
          if (restaurantAd.body) {
            return of(restaurantAd.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new RestaurantAd());
  }
}
