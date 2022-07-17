import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AdComponent } from '../list/ad.component';
import { AdDetailComponent } from '../detail/ad-detail.component';
import { AdUpdateComponent } from '../update/ad-update.component';
import { AdRoutingResolveService } from './ad-routing-resolve.service';

const adRoute: Routes = [
  {
    path: '',
    component: AdComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AdDetailComponent,
    resolve: {
      ad: AdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AdUpdateComponent,
    resolve: {
      ad: AdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AdUpdateComponent,
    resolve: {
      ad: AdRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(adRoute)],
  exports: [RouterModule],
})
export class AdRoutingModule {}
