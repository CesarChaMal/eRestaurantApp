import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NewOrderComponent } from '../list/new-order.component';
import { NewOrderDetailComponent } from '../detail/new-order-detail.component';
import { NewOrderUpdateComponent } from '../update/new-order-update.component';
import { NewOrderRoutingResolveService } from './new-order-routing-resolve.service';

const newOrderRoute: Routes = [
  {
    path: '',
    component: NewOrderComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NewOrderDetailComponent,
    resolve: {
      newOrder: NewOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NewOrderUpdateComponent,
    resolve: {
      newOrder: NewOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NewOrderUpdateComponent,
    resolve: {
      newOrder: NewOrderRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(newOrderRoute)],
  exports: [RouterModule],
})
export class NewOrderRoutingModule {}
