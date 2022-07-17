import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AppAdComponent } from '../list/app-ad.component';
import { AppAdDetailComponent } from '../detail/app-ad-detail.component';
import { AppAdUpdateComponent } from '../update/app-ad-update.component';
import { AppAdRoutingResolveService } from './app-ad-routing-resolve.service';

const appAdRoute: Routes = [
  {
    path: '',
    component: AppAdComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AppAdDetailComponent,
    resolve: {
      appAd: AppAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AppAdUpdateComponent,
    resolve: {
      appAd: AppAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AppAdUpdateComponent,
    resolve: {
      appAd: AppAdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(appAdRoute)],
  exports: [RouterModule],
})
export class AppAdRoutingModule {}
