import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { INotificationType, getNotificationTypeIdentifier } from '../notification-type.model';

export type EntityResponseType = HttpResponse<INotificationType>;
export type EntityArrayResponseType = HttpResponse<INotificationType[]>;

@Injectable({ providedIn: 'root' })
export class NotificationTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notification-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/notification-types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notificationType: INotificationType): Observable<EntityResponseType> {
    return this.http.post<INotificationType>(this.resourceUrl, notificationType, { observe: 'response' });
  }

  update(notificationType: INotificationType): Observable<EntityResponseType> {
    return this.http.put<INotificationType>(
      `${this.resourceUrl}/${getNotificationTypeIdentifier(notificationType) as string}`,
      notificationType,
      { observe: 'response' }
    );
  }

  partialUpdate(notificationType: INotificationType): Observable<EntityResponseType> {
    return this.http.patch<INotificationType>(
      `${this.resourceUrl}/${getNotificationTypeIdentifier(notificationType) as string}`,
      notificationType,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<INotificationType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotificationType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotificationType[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addNotificationTypeToCollectionIfMissing(
    notificationTypeCollection: INotificationType[],
    ...notificationTypesToCheck: (INotificationType | null | undefined)[]
  ): INotificationType[] {
    const notificationTypes: INotificationType[] = notificationTypesToCheck.filter(isPresent);
    if (notificationTypes.length > 0) {
      const notificationTypeCollectionIdentifiers = notificationTypeCollection.map(
        notificationTypeItem => getNotificationTypeIdentifier(notificationTypeItem)!
      );
      const notificationTypesToAdd = notificationTypes.filter(notificationTypeItem => {
        const notificationTypeIdentifier = getNotificationTypeIdentifier(notificationTypeItem);
        if (notificationTypeIdentifier == null || notificationTypeCollectionIdentifiers.includes(notificationTypeIdentifier)) {
          return false;
        }
        notificationTypeCollectionIdentifiers.push(notificationTypeIdentifier);
        return true;
      });
      return [...notificationTypesToAdd, ...notificationTypeCollection];
    }
    return notificationTypeCollection;
  }
}
