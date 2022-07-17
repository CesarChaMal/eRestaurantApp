import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppDiscount, AppDiscount } from '../app-discount.model';
import { AppDiscountService } from '../service/app-discount.service';

@Injectable({ providedIn: 'root' })
export class AppDiscountRoutingResolveService implements Resolve<IAppDiscount> {
  constructor(protected service: AppDiscountService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppDiscount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((appDiscount: HttpResponse<AppDiscount>) => {
          if (appDiscount.body) {
            return of(appDiscount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AppDiscount());
  }
}
