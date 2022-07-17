import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderTypeComponent } from './list/order-type.component';
import { OrderTypeDetailComponent } from './detail/order-type-detail.component';
import { OrderTypeUpdateComponent } from './update/order-type-update.component';
import { OrderTypeDeleteDialogComponent } from './delete/order-type-delete-dialog.component';
import { OrderTypeRoutingModule } from './route/order-type-routing.module';

@NgModule({
  imports: [SharedModule, OrderTypeRoutingModule],
  declarations: [OrderTypeComponent, OrderTypeDetailComponent, OrderTypeUpdateComponent, OrderTypeDeleteDialogComponent],
  entryComponents: [OrderTypeDeleteDialogComponent],
})
export class OrderTypeModule {}
