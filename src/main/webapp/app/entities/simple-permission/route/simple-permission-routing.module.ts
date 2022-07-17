import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SimplePermissionComponent } from '../list/simple-permission.component';
import { SimplePermissionDetailComponent } from '../detail/simple-permission-detail.component';
import { SimplePermissionUpdateComponent } from '../update/simple-permission-update.component';
import { SimplePermissionRoutingResolveService } from './simple-permission-routing-resolve.service';

const simplePermissionRoute: Routes = [
  {
    path: '',
    component: SimplePermissionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SimplePermissionDetailComponent,
    resolve: {
      simplePermission: SimplePermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SimplePermissionUpdateComponent,
    resolve: {
      simplePermission: SimplePermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SimplePermissionUpdateComponent,
    resolve: {
      simplePermission: SimplePermissionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(simplePermissionRoute)],
  exports: [RouterModule],
})
export class SimplePermissionRoutingModule {}
