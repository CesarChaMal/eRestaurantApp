import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPermissionComposite } from '../permission-composite.model';
import { PermissionCompositeService } from '../service/permission-composite.service';
import { PermissionCompositeDeleteDialogComponent } from '../delete/permission-composite-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-permission-composite',
  templateUrl: './permission-composite.component.html',
})
export class PermissionCompositeComponent implements OnInit {
  permissionComposites?: IPermissionComposite[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected permissionCompositeService: PermissionCompositeService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.permissionCompositeService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IPermissionComposite[]>) => {
            this.isLoading = false;
            this.permissionComposites = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.permissionCompositeService.query().subscribe({
      next: (res: HttpResponse<IPermissionComposite[]>) => {
        this.isLoading = false;
        this.permissionComposites = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPermissionComposite): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(permissionComposite: IPermissionComposite): void {
    const modalRef = this.modalService.open(PermissionCompositeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.permissionComposite = permissionComposite;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
