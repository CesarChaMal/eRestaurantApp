import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IComplete, Complete } from '../complete.model';
import { CompleteService } from '../service/complete.service';

@Injectable({ providedIn: 'root' })
export class CompleteRoutingResolveService implements Resolve<IComplete> {
  constructor(protected service: CompleteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IComplete> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((complete: HttpResponse<Complete>) => {
          if (complete.body) {
            return of(complete.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Complete());
  }
}
