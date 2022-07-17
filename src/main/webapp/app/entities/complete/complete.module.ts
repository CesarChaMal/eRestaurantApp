import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CompleteComponent } from './list/complete.component';
import { CompleteDetailComponent } from './detail/complete-detail.component';
import { CompleteUpdateComponent } from './update/complete-update.component';
import { CompleteDeleteDialogComponent } from './delete/complete-delete-dialog.component';
import { CompleteRoutingModule } from './route/complete-routing.module';

@NgModule({
  imports: [SharedModule, CompleteRoutingModule],
  declarations: [CompleteComponent, CompleteDetailComponent, CompleteUpdateComponent, CompleteDeleteDialogComponent],
  entryComponents: [CompleteDeleteDialogComponent],
})
export class CompleteModule {}
