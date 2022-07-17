import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { INotification, getNotificationIdentifier } from '../notification.model';

export type EntityResponseType = HttpResponse<INotification>;
export type EntityArrayResponseType = HttpResponse<INotification[]>;

@Injectable({ providedIn: 'root' })
export class NotificationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/notifications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notification: INotification): Observable<EntityResponseType> {
    return this.http.post<INotification>(this.resourceUrl, notification, { observe: 'response' });
  }

  update(notification: INotification): Observable<EntityResponseType> {
    return this.http.put<INotification>(`${this.resourceUrl}/${getNotificationIdentifier(notification) as string}`, notification, {
      observe: 'response',
    });
  }

  partialUpdate(notification: INotification): Observable<EntityResponseType> {
    return this.http.patch<INotification>(`${this.resourceUrl}/${getNotificationIdentifier(notification) as string}`, notification, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<INotification>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotification[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotification[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addNotificationToCollectionIfMissing(
    notificationCollection: INotification[],
    ...notificationsToCheck: (INotification | null | undefined)[]
  ): INotification[] {
    const notifications: INotification[] = notificationsToCheck.filter(isPresent);
    if (notifications.length > 0) {
      const notificationCollectionIdentifiers = notificationCollection.map(
        notificationItem => getNotificationIdentifier(notificationItem)!
      );
      const notificationsToAdd = notifications.filter(notificationItem => {
        const notificationIdentifier = getNotificationIdentifier(notificationItem);
        if (notificationIdentifier == null || notificationCollectionIdentifiers.includes(notificationIdentifier)) {
          return false;
        }
        notificationCollectionIdentifiers.push(notificationIdentifier);
        return true;
      });
      return [...notificationsToAdd, ...notificationCollection];
    }
    return notificationCollection;
  }
}
