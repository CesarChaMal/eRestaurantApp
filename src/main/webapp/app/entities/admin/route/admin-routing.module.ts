import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AdminComponent } from '../list/admin.component';
import { AdminDetailComponent } from '../detail/admin-detail.component';
import { AdminUpdateComponent } from '../update/admin-update.component';
import { AdminRoutingResolveService } from './admin-routing-resolve.service';

const adminRoute: Routes = [
  {
    path: '',
    component: AdminComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AdminDetailComponent,
    resolve: {
      admin: AdminRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AdminUpdateComponent,
    resolve: {
      admin: AdminRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AdminUpdateComponent,
    resolve: {
      admin: AdminRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(adminRoute)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
