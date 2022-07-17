import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICategories, Categories } from '../categories.model';
import { CategoriesService } from '../service/categories.service';

@Injectable({ providedIn: 'root' })
export class CategoriesRoutingResolveService implements Resolve<ICategories> {
  constructor(protected service: CategoriesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICategories> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((categories: HttpResponse<Categories>) => {
          if (categories.body) {
            return of(categories.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Categories());
  }
}
