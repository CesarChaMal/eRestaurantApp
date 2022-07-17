import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CustomerProfileComponent } from '../list/customer-profile.component';
import { CustomerProfileDetailComponent } from '../detail/customer-profile-detail.component';
import { CustomerProfileUpdateComponent } from '../update/customer-profile-update.component';
import { CustomerProfileRoutingResolveService } from './customer-profile-routing-resolve.service';

const customerProfileRoute: Routes = [
  {
    path: '',
    component: CustomerProfileComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CustomerProfileDetailComponent,
    resolve: {
      customerProfile: CustomerProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CustomerProfileUpdateComponent,
    resolve: {
      customerProfile: CustomerProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CustomerProfileUpdateComponent,
    resolve: {
      customerProfile: CustomerProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(customerProfileRoute)],
  exports: [RouterModule],
})
export class CustomerProfileRoutingModule {}
