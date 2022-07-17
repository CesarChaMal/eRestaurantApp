import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RestaurantComponent } from '../list/restaurant.component';
import { RestaurantDetailComponent } from '../detail/restaurant-detail.component';
import { RestaurantUpdateComponent } from '../update/restaurant-update.component';
import { RestaurantRoutingResolveService } from './restaurant-routing-resolve.service';

const restaurantRoute: Routes = [
  {
    path: '',
    component: RestaurantComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RestaurantDetailComponent,
    resolve: {
      restaurant: RestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RestaurantUpdateComponent,
    resolve: {
      restaurant: RestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RestaurantUpdateComponent,
    resolve: {
      restaurant: RestaurantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(restaurantRoute)],
  exports: [RouterModule],
})
export class RestaurantRoutingModule {}
