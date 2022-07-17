import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RestaurantDiscountComponent } from './list/restaurant-discount.component';
import { RestaurantDiscountDetailComponent } from './detail/restaurant-discount-detail.component';
import { RestaurantDiscountUpdateComponent } from './update/restaurant-discount-update.component';
import { RestaurantDiscountDeleteDialogComponent } from './delete/restaurant-discount-delete-dialog.component';
import { RestaurantDiscountRoutingModule } from './route/restaurant-discount-routing.module';

@NgModule({
  imports: [SharedModule, RestaurantDiscountRoutingModule],
  declarations: [
    RestaurantDiscountComponent,
    RestaurantDiscountDetailComponent,
    RestaurantDiscountUpdateComponent,
    RestaurantDiscountDeleteDialogComponent,
  ],
  entryComponents: [RestaurantDiscountDeleteDialogComponent],
})
export class RestaurantDiscountModule {}
