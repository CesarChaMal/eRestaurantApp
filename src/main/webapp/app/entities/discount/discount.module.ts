import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DiscountComponent } from './list/discount.component';
import { DiscountDetailComponent } from './detail/discount-detail.component';
import { DiscountUpdateComponent } from './update/discount-update.component';
import { DiscountDeleteDialogComponent } from './delete/discount-delete-dialog.component';
import { DiscountRoutingModule } from './route/discount-routing.module';

@NgModule({
  imports: [SharedModule, DiscountRoutingModule],
  declarations: [DiscountComponent, DiscountDetailComponent, DiscountUpdateComponent, DiscountDeleteDialogComponent],
  entryComponents: [DiscountDeleteDialogComponent],
})
export class DiscountModule {}
