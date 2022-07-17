import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AppUserComponent } from '../list/app-user.component';
import { AppUserDetailComponent } from '../detail/app-user-detail.component';
import { AppUserUpdateComponent } from '../update/app-user-update.component';
import { AppUserRoutingResolveService } from './app-user-routing-resolve.service';

const appUserRoute: Routes = [
  {
    path: '',
    component: AppUserComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AppUserDetailComponent,
    resolve: {
      appUser: AppUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AppUserUpdateComponent,
    resolve: {
      appUser: AppUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AppUserUpdateComponent,
    resolve: {
      appUser: AppUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appUserRoute)],
  exports: [RouterModule],
})
export class AppUserRoutingModule {}
