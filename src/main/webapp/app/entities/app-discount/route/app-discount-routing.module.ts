import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AppDiscountComponent } from '../list/app-discount.component';
import { AppDiscountDetailComponent } from '../detail/app-discount-detail.component';
import { AppDiscountUpdateComponent } from '../update/app-discount-update.component';
import { AppDiscountRoutingResolveService } from './app-discount-routing-resolve.service';

const appDiscountRoute: Routes = [
  {
    path: '',
    component: AppDiscountComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AppDiscountDetailComponent,
    resolve: {
      appDiscount: AppDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AppDiscountUpdateComponent,
    resolve: {
      appDiscount: AppDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AppDiscountUpdateComponent,
    resolve: {
      appDiscount: AppDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appDiscountRoute)],
  exports: [RouterModule],
})
export class AppDiscountRoutingModule {}
