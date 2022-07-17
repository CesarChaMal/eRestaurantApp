import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SimplePermissionComponent } from './list/simple-permission.component';
import { SimplePermissionDetailComponent } from './detail/simple-permission-detail.component';
import { SimplePermissionUpdateComponent } from './update/simple-permission-update.component';
import { SimplePermissionDeleteDialogComponent } from './delete/simple-permission-delete-dialog.component';
import { SimplePermissionRoutingModule } from './route/simple-permission-routing.module';

@NgModule({
  imports: [SharedModule, SimplePermissionRoutingModule],
  declarations: [
    SimplePermissionComponent,
    SimplePermissionDetailComponent,
    SimplePermissionUpdateComponent,
    SimplePermissionDeleteDialogComponent,
  ],
  entryComponents: [SimplePermissionDeleteDialogComponent],
})
export class SimplePermissionModule {}
