import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderType, OrderType } from '../order-type.model';
import { OrderTypeService } from '../service/order-type.service';

@Injectable({ providedIn: 'root' })
export class OrderTypeRoutingResolveService implements Resolve<IOrderType> {
  constructor(protected service: OrderTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderType: HttpResponse<OrderType>) => {
          if (orderType.body) {
            return of(orderType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OrderType());
  }
}
