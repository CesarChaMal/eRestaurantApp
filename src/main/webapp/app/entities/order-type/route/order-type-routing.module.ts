import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrderTypeComponent } from '../list/order-type.component';
import { OrderTypeDetailComponent } from '../detail/order-type-detail.component';
import { OrderTypeUpdateComponent } from '../update/order-type-update.component';
import { OrderTypeRoutingResolveService } from './order-type-routing-resolve.service';

const orderTypeRoute: Routes = [
  {
    path: '',
    component: OrderTypeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrderTypeDetailComponent,
    resolve: {
      orderType: OrderTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrderTypeUpdateComponent,
    resolve: {
      orderType: OrderTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrderTypeUpdateComponent,
    resolve: {
      orderType: OrderTypeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderTypeRoute)],
  exports: [RouterModule],
})
export class OrderTypeRoutingModule {}
