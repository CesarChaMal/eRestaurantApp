import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRestaurantDiscount, RestaurantDiscount } from '../restaurant-discount.model';
import { RestaurantDiscountService } from '../service/restaurant-discount.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IProducts } from 'app/entities/products/products.model';
import { ProductsService } from 'app/entities/products/service/products.service';

@Component({
  selector: 'jhi-restaurant-discount-update',
  templateUrl: './restaurant-discount-update.component.html',
})
export class RestaurantDiscountUpdateComponent implements OnInit {
  isSaving = false;

  productsSharedCollection: IProducts[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    code: [null, [Validators.required, Validators.minLength(3)]],
    description: [],
    percentage: [null, [Validators.required]],
    products: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected restaurantDiscountService: RestaurantDiscountService,
    protected productsService: ProductsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ restaurantDiscount }) => {
      this.updateForm(restaurantDiscount);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('eRestaurantApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const restaurantDiscount = this.createFromForm();
    if (restaurantDiscount.id !== undefined) {
      this.subscribeToSaveResponse(this.restaurantDiscountService.update(restaurantDiscount));
    } else {
      this.subscribeToSaveResponse(this.restaurantDiscountService.create(restaurantDiscount));
    }
  }

  trackProductsById(_index: number, item: IProducts): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRestaurantDiscount>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(restaurantDiscount: IRestaurantDiscount): void {
    this.editForm.patchValue({
      id: restaurantDiscount.id,
      code: restaurantDiscount.code,
      description: restaurantDiscount.description,
      percentage: restaurantDiscount.percentage,
      products: restaurantDiscount.products,
    });

    this.productsSharedCollection = this.productsService.addProductsToCollectionIfMissing(
      this.productsSharedCollection,
      restaurantDiscount.products
    );
  }

  protected loadRelationshipsOptions(): void {
    this.productsService
      .query()
      .pipe(map((res: HttpResponse<IProducts[]>) => res.body ?? []))
      .pipe(
        map((products: IProducts[]) =>
          this.productsService.addProductsToCollectionIfMissing(products, this.editForm.get('products')!.value)
        )
      )
      .subscribe((products: IProducts[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): IRestaurantDiscount {
    return {
      ...new RestaurantDiscount(),
      id: this.editForm.get(['id'])!.value,
      code: this.editForm.get(['code'])!.value,
      description: this.editForm.get(['description'])!.value,
      percentage: this.editForm.get(['percentage'])!.value,
      products: this.editForm.get(['products'])!.value,
    };
  }
}
