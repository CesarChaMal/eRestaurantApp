import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PermissionCompositeComponent } from './list/permission-composite.component';
import { PermissionCompositeDetailComponent } from './detail/permission-composite-detail.component';
import { PermissionCompositeUpdateComponent } from './update/permission-composite-update.component';
import { PermissionCompositeDeleteDialogComponent } from './delete/permission-composite-delete-dialog.component';
import { PermissionCompositeRoutingModule } from './route/permission-composite-routing.module';

@NgModule({
  imports: [SharedModule, PermissionCompositeRoutingModule],
  declarations: [
    PermissionCompositeComponent,
    PermissionCompositeDetailComponent,
    PermissionCompositeUpdateComponent,
    PermissionCompositeDeleteDialogComponent,
  ],
  entryComponents: [PermissionCompositeDeleteDialogComponent],
})
export class PermissionCompositeModule {}
