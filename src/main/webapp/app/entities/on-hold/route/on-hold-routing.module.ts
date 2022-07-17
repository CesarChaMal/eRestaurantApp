import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OnHoldComponent } from '../list/on-hold.component';
import { OnHoldDetailComponent } from '../detail/on-hold-detail.component';
import { OnHoldUpdateComponent } from '../update/on-hold-update.component';
import { OnHoldRoutingResolveService } from './on-hold-routing-resolve.service';

const onHoldRoute: Routes = [
  {
    path: '',
    component: OnHoldComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OnHoldDetailComponent,
    resolve: {
      onHold: OnHoldRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OnHoldUpdateComponent,
    resolve: {
      onHold: OnHoldRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OnHoldUpdateComponent,
    resolve: {
      onHold: OnHoldRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(onHoldRoute)],
  exports: [RouterModule],
})
export class OnHoldRoutingModule {}
