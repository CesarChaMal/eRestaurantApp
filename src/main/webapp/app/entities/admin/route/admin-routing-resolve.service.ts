import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAdmin, Admin } from '../admin.model';
import { AdminService } from '../service/admin.service';

@Injectable({ providedIn: 'root' })
export class AdminRoutingResolveService implements Resolve<IAdmin> {
  constructor(protected service: AdminService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAdmin> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((admin: HttpResponse<Admin>) => {
          if (admin.body) {
            return of(admin.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Admin());
  }
}
