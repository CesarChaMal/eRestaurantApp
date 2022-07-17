import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RoleComponent } from '../list/role.component';
import { RoleDetailComponent } from '../detail/role-detail.component';
import { RoleUpdateComponent } from '../update/role-update.component';
import { RoleRoutingResolveService } from './role-routing-resolve.service';

const roleRoute: Routes = [
  {
    path: '',
    component: RoleComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RoleDetailComponent,
    resolve: {
      role: RoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RoleUpdateComponent,
    resolve: {
      role: RoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RoleUpdateComponent,
    resolve: {
      role: RoleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(roleRoute)],
  exports: [RouterModule],
})
export class RoleRoutingModule {}
