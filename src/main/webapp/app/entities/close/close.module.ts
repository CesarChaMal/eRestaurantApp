import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CloseComponent } from './list/close.component';
import { CloseDetailComponent } from './detail/close-detail.component';
import { CloseUpdateComponent } from './update/close-update.component';
import { CloseDeleteDialogComponent } from './delete/close-delete-dialog.component';
import { CloseRoutingModule } from './route/close-routing.module';

@NgModule({
  imports: [SharedModule, CloseRoutingModule],
  declarations: [CloseComponent, CloseDetailComponent, CloseUpdateComponent, CloseDeleteDialogComponent],
  entryComponents: [CloseDeleteDialogComponent],
})
export class CloseModule {}
