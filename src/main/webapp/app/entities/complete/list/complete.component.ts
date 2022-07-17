import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IComplete } from '../complete.model';
import { CompleteService } from '../service/complete.service';
import { CompleteDeleteDialogComponent } from '../delete/complete-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-complete',
  templateUrl: './complete.component.html',
})
export class CompleteComponent implements OnInit {
  completes?: IComplete[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected completeService: CompleteService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.completeService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IComplete[]>) => {
            this.isLoading = false;
            this.completes = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.completeService.query().subscribe({
      next: (res: HttpResponse<IComplete[]>) => {
        this.isLoading = false;
        this.completes = res.body ?? [];
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

  trackId(_index: number, item: IComplete): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(complete: IComplete): void {
    const modalRef = this.modalService.open(CompleteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.complete = complete;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
