import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PermissionCompositeComponent } from '../list/permission-composite.component';
import { PermissionCompositeDetailComponent } from '../detail/permission-composite-detail.component';
import { PermissionCompositeUpdateComponent } from '../update/permission-composite-update.component';
import { PermissionCompositeRoutingResolveService } from './permission-composite-routing-resolve.service';

const permissionCompositeRoute: Routes = [
  {
    path: '',
    component: PermissionCompositeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PermissionCompositeDetailComponent,
    resolve: {
      permissionComposite: PermissionCompositeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PermissionCompositeUpdateComponent,
    resolve: {
      permissionComposite: PermissionCompositeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PermissionCompositeUpdateComponent,
    resolve: {
      permissionComposite: PermissionCompositeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(permissionCompositeRoute)],
  exports: [RouterModule],
})
export class PermissionCompositeRoutingModule {}
