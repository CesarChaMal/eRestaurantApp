import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IState } from '../state.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-state-detail',
  templateUrl: './state-detail.component.html',
})
export class StateDetailComponent implements OnInit {
  state: IState | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ state }) => {
      this.state = state;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
