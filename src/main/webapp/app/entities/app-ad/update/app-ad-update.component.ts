import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAppAd, AppAd } from '../app-ad.model';
import { AppAdService } from '../service/app-ad.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IAdmin } from 'app/entities/admin/admin.model';
import { AdminService } from 'app/entities/admin/service/admin.service';

@Component({
  selector: 'jhi-app-ad-update',
  templateUrl: './app-ad-update.component.html',
})
export class AppAdUpdateComponent implements OnInit {
  isSaving = false;

  adminsSharedCollection: IAdmin[] = [];

  editForm = this.fb.group({
    id: [null, [Validators.required, Validators.minLength(5)]],
    url: [null, [Validators.required, Validators.minLength(5)]],
    description: [],
    admin: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected appAdService: AppAdService,
    protected adminService: AdminService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appAd }) => {
      this.updateForm(appAd);

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
    const appAd = this.createFromForm();
    if (appAd.id !== undefined) {
      this.subscribeToSaveResponse(this.appAdService.update(appAd));
    } else {
      this.subscribeToSaveResponse(this.appAdService.create(appAd));
    }
  }

  trackAdminById(_index: number, item: IAdmin): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppAd>>): void {
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

  protected updateForm(appAd: IAppAd): void {
    this.editForm.patchValue({
      id: appAd.id,
      url: appAd.url,
      description: appAd.description,
      admin: appAd.admin,
    });

    this.adminsSharedCollection = this.adminService.addAdminToCollectionIfMissing(this.adminsSharedCollection, appAd.admin);
  }

  protected loadRelationshipsOptions(): void {
    this.adminService
      .query()
      .pipe(map((res: HttpResponse<IAdmin[]>) => res.body ?? []))
      .pipe(map((admins: IAdmin[]) => this.adminService.addAdminToCollectionIfMissing(admins, this.editForm.get('admin')!.value)))
      .subscribe((admins: IAdmin[]) => (this.adminsSharedCollection = admins));
  }

  protected createFromForm(): IAppAd {
    return {
      ...new AppAd(),
      id: this.editForm.get(['id'])!.value,
      url: this.editForm.get(['url'])!.value,
      description: this.editForm.get(['description'])!.value,
      admin: this.editForm.get(['admin'])!.value,
    };
  }
}
