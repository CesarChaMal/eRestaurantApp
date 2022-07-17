import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CategoriesComponent } from '../list/categories.component';
import { CategoriesDetailComponent } from '../detail/categories-detail.component';
import { CategoriesUpdateComponent } from '../update/categories-update.component';
import { CategoriesRoutingResolveService } from './categories-routing-resolve.service';

const categoriesRoute: Routes = [
  {
    path: '',
    component: CategoriesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CategoriesDetailComponent,
    resolve: {
      categories: CategoriesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CategoriesUpdateComponent,
    resolve: {
      categories: CategoriesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CategoriesUpdateComponent,
    resolve: {
      categories: CategoriesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(categoriesRoute)],
  exports: [RouterModule],
})
export class CategoriesRoutingModule {}
