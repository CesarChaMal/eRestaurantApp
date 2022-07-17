import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CancelComponent } from '../list/cancel.component';
import { CancelDetailComponent } from '../detail/cancel-detail.component';
import { CancelUpdateComponent } from '../update/cancel-update.component';
import { CancelRoutingResolveService } from './cancel-routing-resolve.service';

const cancelRoute: Routes = [
  {
    path: '',
    component: CancelComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CancelDetailComponent,
    resolve: {
      cancel: CancelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CancelUpdateComponent,
    resolve: {
      cancel: CancelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CancelUpdateComponent,
    resolve: {
      cancel: CancelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(cancelRoute)],
  exports: [RouterModule],
})
export class CancelRoutingModule {}
