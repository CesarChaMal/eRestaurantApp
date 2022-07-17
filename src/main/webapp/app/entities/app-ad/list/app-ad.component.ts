import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppAd } from '../app-ad.model';
import { AppAdService } from '../service/app-ad.service';
import { AppAdDeleteDialogComponent } from '../delete/app-ad-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-app-ad',
  templateUrl: './app-ad.component.html',
})
export class AppAdComponent implements OnInit {
  appAds?: IAppAd[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected appAdService: AppAdService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.appAdService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IAppAd[]>) => {
            this.isLoading = false;
            this.appAds = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.appAdService.query().subscribe({
      next: (res: HttpResponse<IAppAd[]>) => {
        this.isLoading = false;
        this.appAds = res.body ?? [];
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

  trackId(_index: number, item: IAppAd): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(appAd: IAppAd): void {
    const modalRef = this.modalService.open(AppAdDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.appAd = appAd;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
