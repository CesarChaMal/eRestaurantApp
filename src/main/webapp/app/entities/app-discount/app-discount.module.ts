import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AppDiscountComponent } from './list/app-discount.component';
import { AppDiscountDetailComponent } from './detail/app-discount-detail.component';
import { AppDiscountUpdateComponent } from './update/app-discount-update.component';
import { AppDiscountDeleteDialogComponent } from './delete/app-discount-delete-dialog.component';
import { AppDiscountRoutingModule } from './route/app-discount-routing.module';

@NgModule({
  imports: [SharedModule, AppDiscountRoutingModule],
  declarations: [AppDiscountComponent, AppDiscountDetailComponent, AppDiscountUpdateComponent, AppDiscountDeleteDialogComponent],
  entryComponents: [AppDiscountDeleteDialogComponent],
})
export class AppDiscountModule {}
