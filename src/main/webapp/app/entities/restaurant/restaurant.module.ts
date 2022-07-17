import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RestaurantComponent } from './list/restaurant.component';
import { RestaurantDetailComponent } from './detail/restaurant-detail.component';
import { RestaurantUpdateComponent } from './update/restaurant-update.component';
import { RestaurantDeleteDialogComponent } from './delete/restaurant-delete-dialog.component';
import { RestaurantRoutingModule } from './route/restaurant-routing.module';

@NgModule({
  imports: [SharedModule, RestaurantRoutingModule],
  declarations: [RestaurantComponent, RestaurantDetailComponent, RestaurantUpdateComponent, RestaurantDeleteDialogComponent],
  entryComponents: [RestaurantDeleteDialogComponent],
})
export class RestaurantModule {}
