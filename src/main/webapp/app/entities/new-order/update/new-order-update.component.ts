import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { INewOrder, NewOrder } from '../new-order.model';
import { NewOrderService } from '../service/new-order.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-new-order-update',
  templateUrl: './new-order-update.component.html',
})
export class NewOrderUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    enabled: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected newOrderService: NewOrderService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ newOrder }) => {
      this.updateForm(newOrder);
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
    const newOrder = this.createFromForm();
    if (newOrder.id !== undefined) {
      this.subscribeToSaveResponse(this.newOrderService.update(newOrder));
    } else {
      this.subscribeToSaveResponse(this.newOrderService.create(newOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INewOrder>>): void {
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

  protected updateForm(newOrder: INewOrder): void {
    this.editForm.patchValue({
      id: newOrder.id,
      description: newOrder.description,
      enabled: newOrder.enabled,
    });
  }

  protected createFromForm(): INewOrder {
    return {
      ...new NewOrder(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
    };
  }
}
