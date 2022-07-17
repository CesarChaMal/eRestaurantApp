import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CancelComponent } from './list/cancel.component';
import { CancelDetailComponent } from './detail/cancel-detail.component';
import { CancelUpdateComponent } from './update/cancel-update.component';
import { CancelDeleteDialogComponent } from './delete/cancel-delete-dialog.component';
import { CancelRoutingModule } from './route/cancel-routing.module';

@NgModule({
  imports: [SharedModule, CancelRoutingModule],
  declarations: [CancelComponent, CancelDetailComponent, CancelUpdateComponent, CancelDeleteDialogComponent],
  entryComponents: [CancelDeleteDialogComponent],
})
export class CancelModule {}
