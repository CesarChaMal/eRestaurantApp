import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RoleComponent } from './list/role.component';
import { RoleDetailComponent } from './detail/role-detail.component';
import { RoleUpdateComponent } from './update/role-update.component';
import { RoleDeleteDialogComponent } from './delete/role-delete-dialog.component';
import { RoleRoutingModule } from './route/role-routing.module';

@NgModule({
  imports: [SharedModule, RoleRoutingModule],
  declarations: [RoleComponent, RoleDetailComponent, RoleUpdateComponent, RoleDeleteDialogComponent],
  entryComponents: [RoleDeleteDialogComponent],
})
export class RoleModule {}
