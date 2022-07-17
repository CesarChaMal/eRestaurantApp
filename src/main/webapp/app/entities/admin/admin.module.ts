import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AdminComponent } from './list/admin.component';
import { AdminDetailComponent } from './detail/admin-detail.component';
import { AdminUpdateComponent } from './update/admin-update.component';
import { AdminDeleteDialogComponent } from './delete/admin-delete-dialog.component';
import { AdminRoutingModule } from './route/admin-routing.module';

@NgModule({
  imports: [SharedModule, AdminRoutingModule],
  declarations: [AdminComponent, AdminDetailComponent, AdminUpdateComponent, AdminDeleteDialogComponent],
  entryComponents: [AdminDeleteDialogComponent],
})
export class AdminModule {}
