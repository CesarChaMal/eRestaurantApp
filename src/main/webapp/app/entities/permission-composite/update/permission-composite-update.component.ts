import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPermissionComposite, PermissionComposite } from '../permission-composite.model';
import { PermissionCompositeService } from '../service/permission-composite.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';

@Component({
  selector: 'jhi-permission-composite-update',
  templateUrl: './permission-composite-update.component.html',
})
export class PermissionCompositeUpdateComponent implements OnInit {
  isSaving = false;

  permissionsCollection: IPermission[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    permission: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected permissionCompositeService: PermissionCompositeService,
    protected permissionService: PermissionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permissionComposite }) => {
      this.updateForm(permissionComposite);

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
    const permissionComposite = this.createFromForm();
    if (permissionComposite.id !== undefined) {
      this.subscribeToSaveResponse(this.permissionCompositeService.update(permissionComposite));
    } else {
      this.subscribeToSaveResponse(this.permissionCompositeService.create(permissionComposite));
    }
  }

  trackPermissionById(_index: number, item: IPermission): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPermissionComposite>>): void {
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

  protected updateForm(permissionComposite: IPermissionComposite): void {
    this.editForm.patchValue({
      id: permissionComposite.id,
      description: permissionComposite.description,
      permission: permissionComposite.permission,
    });

    this.permissionsCollection = this.permissionService.addPermissionToCollectionIfMissing(
      this.permissionsCollection,
      permissionComposite.permission
    );
  }

  protected loadRelationshipsOptions(): void {
    this.permissionService
      .query({ filter: 'permissioncomposite-is-null' })
      .pipe(map((res: HttpResponse<IPermission[]>) => res.body ?? []))
      .pipe(
        map((permissions: IPermission[]) =>
          this.permissionService.addPermissionToCollectionIfMissing(permissions, this.editForm.get('permission')!.value)
        )
      )
      .subscribe((permissions: IPermission[]) => (this.permissionsCollection = permissions));
  }

  protected createFromForm(): IPermissionComposite {
    return {
      ...new PermissionComposite(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      permission: this.editForm.get(['permission'])!.value,
    };
  }
}
