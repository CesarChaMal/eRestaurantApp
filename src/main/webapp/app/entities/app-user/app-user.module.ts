import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AppUserComponent } from './list/app-user.component';
import { AppUserDetailComponent } from './detail/app-user-detail.component';
import { AppUserUpdateComponent } from './update/app-user-update.component';
import { AppUserDeleteDialogComponent } from './delete/app-user-delete-dialog.component';
import { AppUserRoutingModule } from './route/app-user-routing.module';

@NgModule({
  imports: [SharedModule, AppUserRoutingModule],
  declarations: [AppUserComponent, AppUserDetailComponent, AppUserUpdateComponent, AppUserDeleteDialogComponent],
  entryComponents: [AppUserDeleteDialogComponent],
})
export class AppUserModule {}
