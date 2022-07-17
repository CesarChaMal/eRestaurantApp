import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StateComponent } from './list/state.component';
import { StateDetailComponent } from './detail/state-detail.component';
import { StateUpdateComponent } from './update/state-update.component';
import { StateDeleteDialogComponent } from './delete/state-delete-dialog.component';
import { StateRoutingModule } from './route/state-routing.module';

@NgModule({
  imports: [SharedModule, StateRoutingModule],
  declarations: [StateComponent, StateDetailComponent, StateUpdateComponent, StateDeleteDialogComponent],
  entryComponents: [StateDeleteDialogComponent],
})
export class StateModule {}
