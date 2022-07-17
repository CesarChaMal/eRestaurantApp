import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICategories } from '../categories.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-categories-detail',
  templateUrl: './categories-detail.component.html',
})
export class CategoriesDetailComponent implements OnInit {
  categories: ICategories | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categories }) => {
      this.categories = categories;
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
