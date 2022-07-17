import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOnHold, OnHold } from '../on-hold.model';
import { OnHoldService } from '../service/on-hold.service';

@Injectable({ providedIn: 'root' })
export class OnHoldRoutingResolveService implements Resolve<IOnHold> {
  constructor(protected service: OnHoldService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOnHold> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((onHold: HttpResponse<OnHold>) => {
          if (onHold.body) {
            return of(onHold.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OnHold());
  }
}
