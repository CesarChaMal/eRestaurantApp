import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AdComponent } from './list/ad.component';
import { AdDetailComponent } from './detail/ad-detail.component';
import { AdUpdateComponent } from './update/ad-update.component';
import { AdDeleteDialogComponent } from './delete/ad-delete-dialog.component';
import { AdRoutingModule } from './route/ad-routing.module';

@NgModule({
  imports: [SharedModule, AdRoutingModule],
  declarations: [AdComponent, AdDetailComponent, AdUpdateComponent, AdDeleteDialogComponent],
  entryComponents: [AdDeleteDialogComponent],
})
export class AdModule {}
