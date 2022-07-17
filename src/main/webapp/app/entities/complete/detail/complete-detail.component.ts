import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IComplete } from '../complete.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-complete-detail',
  templateUrl: './complete-detail.component.html',
})
export class CompleteDetailComponent implements OnInit {
  complete: IComplete | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ complete }) => {
      this.complete = complete;
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
