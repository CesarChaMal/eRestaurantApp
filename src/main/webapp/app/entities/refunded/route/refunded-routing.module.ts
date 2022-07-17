import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RefundedComponent } from '../list/refunded.component';
import { RefundedDetailComponent } from '../detail/refunded-detail.component';
import { RefundedUpdateComponent } from '../update/refunded-update.component';
import { RefundedRoutingResolveService } from './refunded-routing-resolve.service';

const refundedRoute: Routes = [
  {
    path: '',
    component: RefundedComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RefundedDetailComponent,
    resolve: {
      refunded: RefundedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RefundedUpdateComponent,
    resolve: {
      refunded: RefundedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RefundedUpdateComponent,
    resolve: {
      refunded: RefundedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(refundedRoute)],
  exports: [RouterModule],
})
export class RefundedRoutingModule {}
