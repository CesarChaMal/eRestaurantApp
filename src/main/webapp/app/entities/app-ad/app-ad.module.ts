import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AppAdComponent } from './list/app-ad.component';
import { AppAdDetailComponent } from './detail/app-ad-detail.component';
import { AppAdUpdateComponent } from './update/app-ad-update.component';
import { AppAdDeleteDialogComponent } from './delete/app-ad-delete-dialog.component';
import { AppAdRoutingModule } from './route/app-ad-routing.module';

@NgModule({
  imports: [SharedModule, AppAdRoutingModule],
  declarations: [AppAdComponent, AppAdDetailComponent, AppAdUpdateComponent, AppAdDeleteDialogComponent],
  entryComponents: [AppAdDeleteDialogComponent],
})
export class AppAdModule {}
