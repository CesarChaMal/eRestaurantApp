import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DiscountComponent } from '../list/discount.component';
import { DiscountDetailComponent } from '../detail/discount-detail.component';
import { DiscountUpdateComponent } from '../update/discount-update.component';
import { DiscountRoutingResolveService } from './discount-routing-resolve.service';

const discountRoute: Routes = [
  {
    path: '',
    component: DiscountComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DiscountDetailComponent,
    resolve: {
      discount: DiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DiscountUpdateComponent,
    resolve: {
      discount: DiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DiscountUpdateComponent,
    resolve: {
      discount: DiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(discountRoute)],
  exports: [RouterModule],
})
export class DiscountRoutingModule {}
