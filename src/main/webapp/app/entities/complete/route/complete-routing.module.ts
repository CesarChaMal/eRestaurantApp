import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CompleteComponent } from '../list/complete.component';
import { CompleteDetailComponent } from '../detail/complete-detail.component';
import { CompleteUpdateComponent } from '../update/complete-update.component';
import { CompleteRoutingResolveService } from './complete-routing-resolve.service';

const completeRoute: Routes = [
  {
    path: '',
    component: CompleteComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CompleteDetailComponent,
    resolve: {
      complete: CompleteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CompleteUpdateComponent,
    resolve: {
      complete: CompleteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CompleteUpdateComponent,
    resolve: {
      complete: CompleteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(completeRoute)],
  exports: [RouterModule],
})
export class CompleteRoutingModule {}
