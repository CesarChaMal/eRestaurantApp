import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAppAd, AppAd } from '../app-ad.model';
import { AppAdService } from '../service/app-ad.service';

@Injectable({ providedIn: 'root' })
export class AppAdRoutingResolveService implements Resolve<IAppAd> {
  constructor(protected service: AppAdService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppAd> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((appAd: HttpResponse<AppAd>) => {
          if (appAd.body) {
            return of(appAd.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AppAd());
  }
}
