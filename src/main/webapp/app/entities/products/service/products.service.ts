import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProducts, getProductsIdentifier } from '../products.model';

export type EntityResponseType = HttpResponse<IProducts>;
export type EntityArrayResponseType = HttpResponse<IProducts[]>;

@Injectable({ providedIn: 'root' })
export class ProductsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/products');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/products');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(products: IProducts): Observable<EntityResponseType> {
    return this.http.post<IProducts>(this.resourceUrl, products, { observe: 'response' });
  }

  update(products: IProducts): Observable<EntityResponseType> {
    return this.http.put<IProducts>(`${this.resourceUrl}/${getProductsIdentifier(products) as string}`, products, { observe: 'response' });
  }

  partialUpdate(products: IProducts): Observable<EntityResponseType> {
    return this.http.patch<IProducts>(`${this.resourceUrl}/${getProductsIdentifier(products) as string}`, products, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IProducts>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProducts[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProducts[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addProductsToCollectionIfMissing(productsCollection: IProducts[], ...productsToCheck: (IProducts | null | undefined)[]): IProducts[] {
    const products: IProducts[] = productsToCheck.filter(isPresent);
    if (products.length > 0) {
      const productsCollectionIdentifiers = productsCollection.map(productsItem => getProductsIdentifier(productsItem)!);
      const productsToAdd = products.filter(productsItem => {
        const productsIdentifier = getProductsIdentifier(productsItem);
        if (productsIdentifier == null || productsCollectionIdentifiers.includes(productsIdentifier)) {
          return false;
        }
        productsCollectionIdentifiers.push(productsIdentifier);
        return true;
      });
      return [...productsToAdd, ...productsCollection];
    }
    return productsCollection;
  }
}
