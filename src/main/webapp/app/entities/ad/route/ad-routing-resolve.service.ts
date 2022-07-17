import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAd, Ad } from '../ad.model';
import { AdService } from '../service/ad.service';

@Injectable({ providedIn: 'root' })
export class AdRoutingResolveService implements Resolve<IAd> {
  constructor(protected service: AdService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAd> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ad: HttpResponse<Ad>) => {
          if (ad.body) {
            return of(ad.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ad());
  }
}
