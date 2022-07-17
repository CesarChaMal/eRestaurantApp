import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICart, Cart } from '../cart.model';
import { CartService } from '../service/cart.service';

@Injectable({ providedIn: 'root' })
export class CartRoutingResolveService implements Resolve<ICart> {
  constructor(protected service: CartService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICart> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cart: HttpResponse<Cart>) => {
          if (cart.body) {
            return of(cart.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Cart());
  }
}
