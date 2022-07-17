import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICancel, Cancel } from '../cancel.model';
import { CancelService } from '../service/cancel.service';

@Injectable({ providedIn: 'root' })
export class CancelRoutingResolveService implements Resolve<ICancel> {
  constructor(protected service: CancelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICancel> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((cancel: HttpResponse<Cancel>) => {
          if (cancel.body) {
            return of(cancel.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Cancel());
  }
}
