import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INotificationType, NotificationType } from '../notification-type.model';
import { NotificationTypeService } from '../service/notification-type.service';

@Injectable({ providedIn: 'root' })
export class NotificationTypeRoutingResolveService implements Resolve<INotificationType> {
  constructor(protected service: NotificationTypeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INotificationType> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((notificationType: HttpResponse<NotificationType>) => {
          if (notificationType.body) {
            return of(notificationType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NotificationType());
  }
}
