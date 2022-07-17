import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NotificationTypeComponent } from './list/notification-type.component';
import { NotificationTypeDetailComponent } from './detail/notification-type-detail.component';
import { NotificationTypeUpdateComponent } from './update/notification-type-update.component';
import { NotificationTypeDeleteDialogComponent } from './delete/notification-type-delete-dialog.component';
import { NotificationTypeRoutingModule } from './route/notification-type-routing.module';

@NgModule({
  imports: [SharedModule, NotificationTypeRoutingModule],
  declarations: [
    NotificationTypeComponent,
    NotificationTypeDetailComponent,
    NotificationTypeUpdateComponent,
    NotificationTypeDeleteDialogComponent,
  ],
  entryComponents: [NotificationTypeDeleteDialogComponent],
})
export class NotificationTypeModule {}
