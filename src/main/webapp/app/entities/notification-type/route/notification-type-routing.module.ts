import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NotificationTypeComponent } from '../list/notification-type.component';
import { NotificationTypeDetailComponent } from '../detail/notification-type-detail.component';
import { NotificationTypeUpdateComponent } from '../update/notification-type-update.component';
import { NotificationTypeRoutingResolveService } from './notification-type-routing-resolve.service';

const notificationTypeRoute: Routes = [
  {
    path: '',
    component: NotificationTypeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotificationTypeDetailComponent,
    resolve: {
      notificationType: NotificationTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotificationTypeUpdateComponent,
    resolve: {
      notificationType: NotificationTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotificationTypeUpdateComponent,
    resolve: {
      notificationType: NotificationTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(notificationTypeRoute)],
  exports: [RouterModule],
})
export class NotificationTypeRoutingModule {}
