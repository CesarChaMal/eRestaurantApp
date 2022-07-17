import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CloseComponent } from '../list/close.component';
import { CloseDetailComponent } from '../detail/close-detail.component';
import { CloseUpdateComponent } from '../update/close-update.component';
import { CloseRoutingResolveService } from './close-routing-resolve.service';

const closeRoute: Routes = [
  {
    path: '',
    component: CloseComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CloseDetailComponent,
    resolve: {
      close: CloseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CloseUpdateComponent,
    resolve: {
      close: CloseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CloseUpdateComponent,
    resolve: {
      close: CloseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(closeRoute)],
  exports: [RouterModule],
})
export class CloseRoutingModule {}
