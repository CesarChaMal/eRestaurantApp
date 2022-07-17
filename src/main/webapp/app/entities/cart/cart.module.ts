import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CartComponent } from './list/cart.component';
import { CartDetailComponent } from './detail/cart-detail.component';
import { CartUpdateComponent } from './update/cart-update.component';
import { CartDeleteDialogComponent } from './delete/cart-delete-dialog.component';
import { CartRoutingModule } from './route/cart-routing.module';

@NgModule({
  imports: [SharedModule, CartRoutingModule],
  declarations: [CartComponent, CartDetailComponent, CartUpdateComponent, CartDeleteDialogComponent],
  entryComponents: [CartDeleteDialogComponent],
})
export class CartModule {}
