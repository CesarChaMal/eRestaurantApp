import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OnHoldComponent } from './list/on-hold.component';
import { OnHoldDetailComponent } from './detail/on-hold-detail.component';
import { OnHoldUpdateComponent } from './update/on-hold-update.component';
import { OnHoldDeleteDialogComponent } from './delete/on-hold-delete-dialog.component';
import { OnHoldRoutingModule } from './route/on-hold-routing.module';

@NgModule({
  imports: [SharedModule, OnHoldRoutingModule],
  declarations: [OnHoldComponent, OnHoldDetailComponent, OnHoldUpdateComponent, OnHoldDeleteDialogComponent],
  entryComponents: [OnHoldDeleteDialogComponent],
})
export class OnHoldModule {}
