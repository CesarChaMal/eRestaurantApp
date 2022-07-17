import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RestaurantAdComponent } from '../list/restaurant-ad.component';
import { RestaurantAdDetailComponent } from '../detail/restaurant-ad-detail.component';
import { RestaurantAdUpdateComponent } from '../update/restaurant-ad-update.component';
import { RestaurantAdRoutingResolveService } from './restaurant-ad-routing-resolve.service';

const restaurantAdRoute: Routes = [
  {
    path: '',
    component: RestaurantAdComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RestaurantAdDetailComponent,
    resolve: {
      restaurantAd: RestaurantAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RestaurantAdUpdateComponent,
    resolve: {
      restaurantAd: RestaurantAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RestaurantAdUpdateComponent,
    resolve: {
      restaurantAd: RestaurantAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(restaurantAdRoute)],
  exports: [RouterModule],
})
export class RestaurantAdRoutingModule {}
