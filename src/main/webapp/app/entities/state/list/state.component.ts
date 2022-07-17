import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IState } from '../state.model';
import { StateService } from '../service/state.service';
import { StateDeleteDialogComponent } from '../delete/state-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-state',
  templateUrl: './state.component.html',
})
export class StateComponent implements OnInit {
  states?: IState[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected stateService: StateService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.stateService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IState[]>) => {
            this.isLoading = false;
            this.states = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.stateService.query().subscribe({
      next: (res: HttpResponse<IState[]>) => {
        this.isLoading = false;
        this.states = res.body ?? [];
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

  trackId(_index: number, item: IState): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(state: IState): void {
    const modalRef = this.modalService.open(StateDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.state = state;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
