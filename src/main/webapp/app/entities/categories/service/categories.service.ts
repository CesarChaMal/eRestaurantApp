import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICategories, getCategoriesIdentifier } from '../categories.model';

export type EntityResponseType = HttpResponse<ICategories>;
export type EntityArrayResponseType = HttpResponse<ICategories[]>;

@Injectable({ providedIn: 'root' })
export class CategoriesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/categories');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/categories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(categories: ICategories): Observable<EntityResponseType> {
    return this.http.post<ICategories>(this.resourceUrl, categories, { observe: 'response' });
  }

  update(categories: ICategories): Observable<EntityResponseType> {
    return this.http.put<ICategories>(`${this.resourceUrl}/${getCategoriesIdentifier(categories) as string}`, categories, {
      observe: 'response',
    });
  }

  partialUpdate(categories: ICategories): Observable<EntityResponseType> {
    return this.http.patch<ICategories>(`${this.resourceUrl}/${getCategoriesIdentifier(categories) as string}`, categories, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<ICategories>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICategories[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICategories[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCategoriesToCollectionIfMissing(
    categoriesCollection: ICategories[],
    ...categoriesToCheck: (ICategories | null | undefined)[]
  ): ICategories[] {
    const categories: ICategories[] = categoriesToCheck.filter(isPresent);
    if (categories.length > 0) {
      const categoriesCollectionIdentifiers = categoriesCollection.map(categoriesItem => getCategoriesIdentifier(categoriesItem)!);
      const categoriesToAdd = categories.filter(categoriesItem => {
        const categoriesIdentifier = getCategoriesIdentifier(categoriesItem);
        if (categoriesIdentifier == null || categoriesCollectionIdentifiers.includes(categoriesIdentifier)) {
          return false;
        }
        categoriesCollectionIdentifiers.push(categoriesIdentifier);
        return true;
      });
      return [...categoriesToAdd, ...categoriesCollection];
    }
    return categoriesCollection;
  }
}
