import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INewOrder, NewOrder } from '../new-order.model';
import { NewOrderService } from '../service/new-order.service';

@Injectable({ providedIn: 'root' })
export class NewOrderRoutingResolveService implements Resolve<INewOrder> {
  constructor(protected service: NewOrderService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INewOrder> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((newOrder: HttpResponse<NewOrder>) => {
          if (newOrder.body) {
            return of(newOrder.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NewOrder());
  }
}
