import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRefunded, Refunded } from '../refunded.model';

import { RefundedService } from './refunded.service';

describe('Refunded Service', () => {
  let service: RefundedService;
  let httpMock: HttpTestingController;
  let elemDefault: IRefunded;
  let expectedResult: IRefunded | IRefunded[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RefundedService);
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

    it('should create a Refunded', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Refunded()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Refunded', () => {
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

    it('should partial update a Refunded', () => {
      const patchObject = Object.assign({}, new Refunded());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Refunded', () => {
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

    it('should delete a Refunded', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRefundedToCollectionIfMissing', () => {
      it('should add a Refunded to an empty array', () => {
        const refunded: IRefunded = { id: 'ABC' };
        expectedResult = service.addRefundedToCollectionIfMissing([], refunded);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(refunded);
      });

      it('should not add a Refunded to an array that contains it', () => {
        const refunded: IRefunded = { id: 'ABC' };
        const refundedCollection: IRefunded[] = [
          {
            ...refunded,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addRefundedToCollectionIfMissing(refundedCollection, refunded);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Refunded to an array that doesn't contain it", () => {
        const refunded: IRefunded = { id: 'ABC' };
        const refundedCollection: IRefunded[] = [{ id: 'CBA' }];
        expectedResult = service.addRefundedToCollectionIfMissing(refundedCollection, refunded);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(refunded);
      });

      it('should add only unique Refunded to an array', () => {
        const refundedArray: IRefunded[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'e2a73efe-bd40-4ae2-9ebf-41779756873d' }];
        const refundedCollection: IRefunded[] = [{ id: 'ABC' }];
        expectedResult = service.addRefundedToCollectionIfMissing(refundedCollection, ...refundedArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const refunded: IRefunded = { id: 'ABC' };
        const refunded2: IRefunded = { id: 'CBA' };
        expectedResult = service.addRefundedToCollectionIfMissing([], refunded, refunded2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(refunded);
        expect(expectedResult).toContain(refunded2);
      });

      it('should accept null and undefined values', () => {
        const refunded: IRefunded = { id: 'ABC' };
        expectedResult = service.addRefundedToCollectionIfMissing([], null, refunded, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(refunded);
      });

      it('should return initial array if no Refunded is added', () => {
        const refundedCollection: IRefunded[] = [{ id: 'ABC' }];
        expectedResult = service.addRefundedToCollectionIfMissing(refundedCollection, undefined, null);
        expect(expectedResult).toEqual(refundedCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
