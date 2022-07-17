import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurant, Restaurant } from '../restaurant.model';
import { RestaurantService } from '../service/restaurant.service';

@Injectable({ providedIn: 'root' })
export class RestaurantRoutingResolveService implements Resolve<IRestaurant> {
  constructor(protected service: RestaurantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRestaurant> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((restaurant: HttpResponse<Restaurant>) => {
          if (restaurant.body) {
            return of(restaurant.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Restaurant());
  }
}
