import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClose, Close } from '../close.model';
import { CloseService } from '../service/close.service';

@Injectable({ providedIn: 'root' })
export class CloseRoutingResolveService implements Resolve<IClose> {
  constructor(protected service: CloseService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClose> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((close: HttpResponse<Close>) => {
          if (close.body) {
            return of(close.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Close());
  }
}
