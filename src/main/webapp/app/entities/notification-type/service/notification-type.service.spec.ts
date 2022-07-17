import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INotificationType, NotificationType } from '../notification-type.model';

import { NotificationTypeService } from './notification-type.service';

describe('NotificationType Service', () => {
  let service: NotificationTypeService;
  let httpMock: HttpTestingController;
  let elemDefault: INotificationType;
  let expectedResult: INotificationType | INotificationType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NotificationTypeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a NotificationType', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new NotificationType()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NotificationType', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a NotificationType', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new NotificationType()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NotificationType', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a NotificationType', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNotificationTypeToCollectionIfMissing', () => {
      it('should add a NotificationType to an empty array', () => {
        const notificationType: INotificationType = { id: 'ABC' };
        expectedResult = service.addNotificationTypeToCollectionIfMissing([], notificationType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationType);
      });

      it('should not add a NotificationType to an array that contains it', () => {
        const notificationType: INotificationType = { id: 'ABC' };
        const notificationTypeCollection: INotificationType[] = [
          {
            ...notificationType,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addNotificationTypeToCollectionIfMissing(notificationTypeCollection, notificationType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NotificationType to an array that doesn't contain it", () => {
        const notificationType: INotificationType = { id: 'ABC' };
        const notificationTypeCollection: INotificationType[] = [{ id: 'CBA' }];
        expectedResult = service.addNotificationTypeToCollectionIfMissing(notificationTypeCollection, notificationType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationType);
      });

      it('should add only unique NotificationType to an array', () => {
        const notificationTypeArray: INotificationType[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '92a0ecc6-debf-4f4d-8967-c8618c28b64a' }];
        const notificationTypeCollection: INotificationType[] = [{ id: 'ABC' }];
        expectedResult = service.addNotificationTypeToCollectionIfMissing(notificationTypeCollection, ...notificationTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notificationType: INotificationType = { id: 'ABC' };
        const notificationType2: INotificationType = { id: 'CBA' };
        expectedResult = service.addNotificationTypeToCollectionIfMissing([], notificationType, notificationType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notificationType);
        expect(expectedResult).toContain(notificationType2);
      });

      it('should accept null and undefined values', () => {
        const notificationType: INotificationType = { id: 'ABC' };
        expectedResult = service.addNotificationTypeToCollectionIfMissing([], null, notificationType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notificationType);
      });

      it('should return initial array if no NotificationType is added', () => {
        const notificationTypeCollection: INotificationType[] = [{ id: 'ABC' }];
        expectedResult = service.addNotificationTypeToCollectionIfMissing(notificationTypeCollection, undefined, null);
        expect(expectedResult).toEqual(notificationTypeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
