import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRole, Role } from '../role.model';
import { RoleService } from '../service/role.service';

@Injectable({ providedIn: 'root' })
export class RoleRoutingResolveService implements Resolve<IRole> {
  constructor(protected service: RoleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRole> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((role: HttpResponse<Role>) => {
          if (role.body) {
            return of(role.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Role());
  }
}
