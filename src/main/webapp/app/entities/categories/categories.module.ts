import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CategoriesComponent } from './list/categories.component';
import { CategoriesDetailComponent } from './detail/categories-detail.component';
import { CategoriesUpdateComponent } from './update/categories-update.component';
import { CategoriesDeleteDialogComponent } from './delete/categories-delete-dialog.component';
import { CategoriesRoutingModule } from './route/categories-routing.module';

@NgModule({
  imports: [SharedModule, CategoriesRoutingModule],
  declarations: [CategoriesComponent, CategoriesDetailComponent, CategoriesUpdateComponent, CategoriesDeleteDialogComponent],
  entryComponents: [CategoriesDeleteDialogComponent],
})
export class CategoriesModule {}
