import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IRole, Role } from '../role.model';
import { RoleService } from '../service/role.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';

@Component({
  selector: 'jhi-role-update',
  templateUrl: './role-update.component.html',
})
export class RoleUpdateComponent implements OnInit {
  isSaving = false;

  permissionsSharedCollection: IPermission[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    permissions: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected roleService: RoleService,
    protected permissionService: PermissionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ role }) => {
      this.updateForm(role);

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
    const role = this.createFromForm();
    if (role.id !== undefined) {
      this.subscribeToSaveResponse(this.roleService.update(role));
    } else {
      this.subscribeToSaveResponse(this.roleService.create(role));
    }
  }

  trackPermissionById(_index: number, item: IPermission): string {
    return item.id!;
  }

  getSelectedPermission(option: IPermission, selectedVals?: IPermission[]): IPermission {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRole>>): void {
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

  protected updateForm(role: IRole): void {
    this.editForm.patchValue({
      id: role.id,
      description: role.description,
      permissions: role.permissions,
    });

    this.permissionsSharedCollection = this.permissionService.addPermissionToCollectionIfMissing(
      this.permissionsSharedCollection,
      ...(role.permissions ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.permissionService
      .query()
      .pipe(map((res: HttpResponse<IPermission[]>) => res.body ?? []))
      .pipe(
        map((permissions: IPermission[]) =>
          this.permissionService.addPermissionToCollectionIfMissing(permissions, ...(this.editForm.get('permissions')!.value ?? []))
        )
      )
      .subscribe((permissions: IPermission[]) => (this.permissionsSharedCollection = permissions));
  }

  protected createFromForm(): IRole {
    return {
      ...new Role(),
      id: this.editForm.get(['id'])!.value,
      description: this.editForm.get(['description'])!.value,
      permissions: this.editForm.get(['permissions'])!.value,
    };
  }
}
