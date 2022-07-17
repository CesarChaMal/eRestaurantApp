import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPermissionComposite, PermissionComposite } from '../permission-composite.model';
import { PermissionCompositeService } from '../service/permission-composite.service';

@Injectable({ providedIn: 'root' })
export class PermissionCompositeRoutingResolveService implements Resolve<IPermissionComposite> {
  constructor(protected service: PermissionCompositeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPermissionComposite> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((permissionComposite: HttpResponse<PermissionComposite>) => {
          if (permissionComposite.body) {
            return of(permissionComposite.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PermissionComposite());
  }
}
