import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RestaurantAdComponent } from './list/restaurant-ad.component';
import { RestaurantAdDetailComponent } from './detail/restaurant-ad-detail.component';
import { RestaurantAdUpdateComponent } from './update/restaurant-ad-update.component';
import { RestaurantAdDeleteDialogComponent } from './delete/restaurant-ad-delete-dialog.component';
import { RestaurantAdRoutingModule } from './route/restaurant-ad-routing.module';

@NgModule({
  imports: [SharedModule, RestaurantAdRoutingModule],
  declarations: [RestaurantAdComponent, RestaurantAdDetailComponent, RestaurantAdUpdateComponent, RestaurantAdDeleteDialogComponent],
  entryComponents: [RestaurantAdDeleteDialogComponent],
})
export class RestaurantAdModule {}
