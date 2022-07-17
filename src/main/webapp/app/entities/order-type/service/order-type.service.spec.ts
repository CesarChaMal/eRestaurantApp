import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrderType, OrderType } from '../order-type.model';

import { OrderTypeService } from './order-type.service';

describe('OrderType Service', () => {
  let service: OrderTypeService;
  let httpMock: HttpTestingController;
  let elemDefault: IOrderType;
  let expectedResult: IOrderType | IOrderType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderTypeService);
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

    it('should create a OrderType', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new OrderType()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderType', () => {
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

    it('should partial update a OrderType', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new OrderType()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderType', () => {
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

    it('should delete a OrderType', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addOrderTypeToCollectionIfMissing', () => {
      it('should add a OrderType to an empty array', () => {
        const orderType: IOrderType = { id: 'ABC' };
        expectedResult = service.addOrderTypeToCollectionIfMissing([], orderType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderType);
      });

      it('should not add a OrderType to an array that contains it', () => {
        const orderType: IOrderType = { id: 'ABC' };
        const orderTypeCollection: IOrderType[] = [
          {
            ...orderType,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addOrderTypeToCollectionIfMissing(orderTypeCollection, orderType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderType to an array that doesn't contain it", () => {
        const orderType: IOrderType = { id: 'ABC' };
        const orderTypeCollection: IOrderType[] = [{ id: 'CBA' }];
        expectedResult = service.addOrderTypeToCollectionIfMissing(orderTypeCollection, orderType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderType);
      });

      it('should add only unique OrderType to an array', () => {
        const orderTypeArray: IOrderType[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'd1891e34-d5f4-492c-b77f-cc967343e015' }];
        const orderTypeCollection: IOrderType[] = [{ id: 'ABC' }];
        expectedResult = service.addOrderTypeToCollectionIfMissing(orderTypeCollection, ...orderTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderType: IOrderType = { id: 'ABC' };
        const orderType2: IOrderType = { id: 'CBA' };
        expectedResult = service.addOrderTypeToCollectionIfMissing([], orderType, orderType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderType);
        expect(expectedResult).toContain(orderType2);
      });

      it('should accept null and undefined values', () => {
        const orderType: IOrderType = { id: 'ABC' };
        expectedResult = service.addOrderTypeToCollectionIfMissing([], null, orderType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderType);
      });

      it('should return initial array if no OrderType is added', () => {
        const orderTypeCollection: IOrderType[] = [{ id: 'ABC' }];
        expectedResult = service.addOrderTypeToCollectionIfMissing(orderTypeCollection, undefined, null);
        expect(expectedResult).toEqual(orderTypeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
