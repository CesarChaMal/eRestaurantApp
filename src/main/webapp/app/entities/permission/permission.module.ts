import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PermissionComponent } from './list/permission.component';
import { PermissionDetailComponent } from './detail/permission-detail.component';
import { PermissionUpdateComponent } from './update/permission-update.component';
import { PermissionDeleteDialogComponent } from './delete/permission-delete-dialog.component';
import { PermissionRoutingModule } from './route/permission-routing.module';

@NgModule({
  imports: [SharedModule, PermissionRoutingModule],
  declarations: [PermissionComponent, PermissionDetailComponent, PermissionUpdateComponent, PermissionDeleteDialogComponent],
  entryComponents: [PermissionDeleteDialogComponent],
})
export class PermissionModule {}
