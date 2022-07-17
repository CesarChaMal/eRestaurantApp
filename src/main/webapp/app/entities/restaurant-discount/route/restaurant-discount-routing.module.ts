import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RestaurantDiscountComponent } from '../list/restaurant-discount.component';
import { RestaurantDiscountDetailComponent } from '../detail/restaurant-discount-detail.component';
import { RestaurantDiscountUpdateComponent } from '../update/restaurant-discount-update.component';
import { RestaurantDiscountRoutingResolveService } from './restaurant-discount-routing-resolve.service';

const restaurantDiscountRoute: Routes = [
  {
    path: '',
    component: RestaurantDiscountComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RestaurantDiscountDetailComponent,
    resolve: {
      restaurantDiscount: RestaurantDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RestaurantDiscountUpdateComponent,
    resolve: {
      restaurantDiscount: RestaurantDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RestaurantDiscountUpdateComponent,
    resolve: {
      restaurantDiscount: RestaurantDiscountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(restaurantDiscountRoute)],
  exports: [RouterModule],
})
export class RestaurantDiscountRoutingModule {}
