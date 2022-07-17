import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PermissionComponent } from '../list/permission.component';
import { PermissionDetailComponent } from '../detail/permission-detail.component';
import { PermissionUpdateComponent } from '../update/permission-update.component';
import { PermissionRoutingResolveService } from './permission-routing-resolve.service';

const permissionRoute: Routes = [
  {
    path: '',
    component: PermissionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PermissionDetailComponent,
    resolve: {
      permission: PermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PermissionUpdateComponent,
    resolve: {
      permission: PermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PermissionUpdateComponent,
    resolve: {
      permission: PermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(permissionRoute)],
  exports: [RouterModule],
})
export class PermissionRoutingModule {}
