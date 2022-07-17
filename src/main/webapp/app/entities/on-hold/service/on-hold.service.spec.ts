import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOnHold, OnHold } from '../on-hold.model';

import { OnHoldService } from './on-hold.service';

describe('OnHold Service', () => {
  let service: OnHoldService;
  let httpMock: HttpTestingController;
  let elemDefault: IOnHold;
  let expectedResult: IOnHold | IOnHold[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OnHoldService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      description: 'AAAAAAA',
      enabled: false,
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

    it('should create a OnHold', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new OnHold()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OnHold', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
          enabled: true,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OnHold', () => {
      const patchObject = Object.assign(
        {
          enabled: true,
        },
        new OnHold()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OnHold', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
          enabled: true,
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

    it('should delete a OnHold', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOnHoldToCollectionIfMissing', () => {
      it('should add a OnHold to an empty array', () => {
        const onHold: IOnHold = { id: 'ABC' };
        expectedResult = service.addOnHoldToCollectionIfMissing([], onHold);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(onHold);
      });

      it('should not add a OnHold to an array that contains it', () => {
        const onHold: IOnHold = { id: 'ABC' };
        const onHoldCollection: IOnHold[] = [
          {
            ...onHold,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addOnHoldToCollectionIfMissing(onHoldCollection, onHold);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OnHold to an array that doesn't contain it", () => {
        const onHold: IOnHold = { id: 'ABC' };
        const onHoldCollection: IOnHold[] = [{ id: 'CBA' }];
        expectedResult = service.addOnHoldToCollectionIfMissing(onHoldCollection, onHold);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(onHold);
      });

      it('should add only unique OnHold to an array', () => {
        const onHoldArray: IOnHold[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '729a66c3-eaa8-4b25-93b9-185456539e4d' }];
        const onHoldCollection: IOnHold[] = [{ id: 'ABC' }];
        expectedResult = service.addOnHoldToCollectionIfMissing(onHoldCollection, ...onHoldArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const onHold: IOnHold = { id: 'ABC' };
        const onHold2: IOnHold = { id: 'CBA' };
        expectedResult = service.addOnHoldToCollectionIfMissing([], onHold, onHold2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(onHold);
        expect(expectedResult).toContain(onHold2);
      });

      it('should accept null and undefined values', () => {
        const onHold: IOnHold = { id: 'ABC' };
        expectedResult = service.addOnHoldToCollectionIfMissing([], null, onHold, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(onHold);
      });

      it('should return initial array if no OnHold is added', () => {
        const onHoldCollection: IOnHold[] = [{ id: 'ABC' }];
        expectedResult = service.addOnHoldToCollectionIfMissing(onHoldCollection, undefined, null);
        expect(expectedResult).toEqual(onHoldCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
