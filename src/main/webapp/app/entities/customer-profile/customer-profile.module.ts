import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CustomerProfileComponent } from './list/customer-profile.component';
import { CustomerProfileDetailComponent } from './detail/customer-profile-detail.component';
import { CustomerProfileUpdateComponent } from './update/customer-profile-update.component';
import { CustomerProfileDeleteDialogComponent } from './delete/customer-profile-delete-dialog.component';
import { CustomerProfileRoutingModule } from './route/customer-profile-routing.module';

@NgModule({
  imports: [SharedModule, CustomerProfileRoutingModule],
  declarations: [
    CustomerProfileComponent,
    CustomerProfileDetailComponent,
    CustomerProfileUpdateComponent,
    CustomerProfileDeleteDialogComponent,
  ],
  entryComponents: [CustomerProfileDeleteDialogComponent],
})
export class CustomerProfileModule {}
