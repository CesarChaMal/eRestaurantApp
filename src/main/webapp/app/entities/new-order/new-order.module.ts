import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NewOrderComponent } from './list/new-order.component';
import { NewOrderDetailComponent } from './detail/new-order-detail.component';
import { NewOrderUpdateComponent } from './update/new-order-update.component';
import { NewOrderDeleteDialogComponent } from './delete/new-order-delete-dialog.component';
import { NewOrderRoutingModule } from './route/new-order-routing.module';

@NgModule({
  imports: [SharedModule, NewOrderRoutingModule],
  declarations: [NewOrderComponent, NewOrderDetailComponent, NewOrderUpdateComponent, NewOrderDeleteDialogComponent],
  entryComponents: [NewOrderDeleteDialogComponent],
})
export class NewOrderModule {}
