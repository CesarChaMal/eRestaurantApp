import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RefundedComponent } from './list/refunded.component';
import { RefundedDetailComponent } from './detail/refunded-detail.component';
import { RefundedUpdateComponent } from './update/refunded-update.component';
import { RefundedDeleteDialogComponent } from './delete/refunded-delete-dialog.component';
import { RefundedRoutingModule } from './route/refunded-routing.module';

@NgModule({
  imports: [SharedModule, RefundedRoutingModule],
  declarations: [RefundedComponent, RefundedDetailComponent, RefundedUpdateComponent, RefundedDeleteDialogComponent],
  entryComponents: [RefundedDeleteDialogComponent],
})
export class RefundedModule {}
