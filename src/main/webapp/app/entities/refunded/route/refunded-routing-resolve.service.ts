import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRefunded, Refunded } from '../refunded.model';
import { RefundedService } from '../service/refunded.service';

@Injectable({ providedIn: 'root' })
export class RefundedRoutingResolveService implements Resolve<IRefunded> {
  constructor(protected service: RefundedService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRefunded> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((refunded: HttpResponse<Refunded>) => {
          if (refunded.body) {
            return of(refunded.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Refunded());
  }
}
