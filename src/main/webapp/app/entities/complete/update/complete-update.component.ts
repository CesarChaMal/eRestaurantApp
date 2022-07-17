import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IComplete, Complete } from '../complete.model';
import { CompleteService } from '../service/complete.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-complete-update',
  templateUrl: './complete-update.component.html',
})
export class CompleteUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    enabled: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected completeService: CompleteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ complete }) => {
      this.updateForm(complete);
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
    const complete = this.createFromForm();
    if (complete.id !== undefined) {
      this.subscribeToSaveResponse(this.completeService.update(complete));
    } else {
      this.subscribeToSaveResponse(this.completeService.create(complete));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComplete>>): void {
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

  protected updateForm(complete: IComplete): void {
    this.editForm.patchValue({
      id: complete.id,
      description: complete.description,
      enabled: complete.enabled,
    });
  }

  protected createFromForm(): IComplete {
    return {
      ...new Complete(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      enabled: this.editForm.get(['enabled'])!.value,
    };
  }
}
