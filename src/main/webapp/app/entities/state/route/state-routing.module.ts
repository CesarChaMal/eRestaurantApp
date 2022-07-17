import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StateComponent } from '../list/state.component';
import { StateDetailComponent } from '../detail/state-detail.component';
import { StateUpdateComponent } from '../update/state-update.component';
import { StateRoutingResolveService } from './state-routing-resolve.service';

const stateRoute: Routes = [
  {
    path: '',
    component: StateComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StateDetailComponent,
    resolve: {
      state: StateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StateUpdateComponent,
    resolve: {
      state: StateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StateUpdateComponent,
    resolve: {
      state: StateRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stateRoute)],
  exports: [RouterModule],
})
export class StateRoutingModule {}
