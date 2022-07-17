import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISimplePermission, SimplePermission } from '../simple-permission.model';
import { SimplePermissionService } from '../service/simple-permission.service';

@Injectable({ providedIn: 'root' })
export class SimplePermissionRoutingResolveService implements Resolve<ISimplePermission> {
  constructor(protected service: SimplePermissionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISimplePermission> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((simplePermission: HttpResponse<SimplePermission>) => {
          if (simplePermission.body) {
            return of(simplePermission.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SimplePermission());
  }
}
