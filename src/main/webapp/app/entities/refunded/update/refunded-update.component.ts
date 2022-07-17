import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IRefunded, Refunded } from '../refunded.model';
import { RefundedService } from '../service/refunded.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-refunded-update',
  templateUrl: './refunded-update.component.html',
})
export class RefundedUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    enabled: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected refundedService: RefundedService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refunded }) => {
      this.updateForm(refunded);
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
    const refunded = this.createFromForm();
    if (refunded.id !== undefined) {
      this.subscribeToSaveResponse(this.refundedService.update(refunded));
    } else {
      this.subscribeToSaveResponse(this.refundedService.create(refunded));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRefunded>>): void {
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

  protected updateForm(refunded: IRefunded): void {
    this.editForm.patchValue({
      id: refunded.id,
      description: refunded.description,
      enabled: refunded.enabled,
    });
  }

  protected createFromForm(): IRefunded {
    return {
      ...new Refunded(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
    };
  }
}
