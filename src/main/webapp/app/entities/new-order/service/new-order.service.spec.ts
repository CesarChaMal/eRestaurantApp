import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INewOrder, NewOrder } from '../new-order.model';

import { NewOrderService } from './new-order.service';

describe('NewOrder Service', () => {
  let service: NewOrderService;
  let httpMock: HttpTestingController;
  let elemDefault: INewOrder;
  let expectedResult: INewOrder | INewOrder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NewOrderService);
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

    it('should create a NewOrder', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new NewOrder()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NewOrder', () => {
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

    it('should partial update a NewOrder', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          enabled: true,
        },
        new NewOrder()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NewOrder', () => {
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

    it('should delete a NewOrder', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNewOrderToCollectionIfMissing', () => {
      it('should add a NewOrder to an empty array', () => {
        const newOrder: INewOrder = { id: 'ABC' };
        expectedResult = service.addNewOrderToCollectionIfMissing([], newOrder);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(newOrder);
      });

      it('should not add a NewOrder to an array that contains it', () => {
        const newOrder: INewOrder = { id: 'ABC' };
        const newOrderCollection: INewOrder[] = [
          {
            ...newOrder,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addNewOrderToCollectionIfMissing(newOrderCollection, newOrder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NewOrder to an array that doesn't contain it", () => {
        const newOrder: INewOrder = { id: 'ABC' };
        const newOrderCollection: INewOrder[] = [{ id: 'CBA' }];
        expectedResult = service.addNewOrderToCollectionIfMissing(newOrderCollection, newOrder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(newOrder);
      });

      it('should add only unique NewOrder to an array', () => {
        const newOrderArray: INewOrder[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '632c16c0-3e68-4bb2-b470-272dfed4eed2' }];
        const newOrderCollection: INewOrder[] = [{ id: 'ABC' }];
        expectedResult = service.addNewOrderToCollectionIfMissing(newOrderCollection, ...newOrderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const newOrder: INewOrder = { id: 'ABC' };
        const newOrder2: INewOrder = { id: 'CBA' };
        expectedResult = service.addNewOrderToCollectionIfMissing([], newOrder, newOrder2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(newOrder);
        expect(expectedResult).toContain(newOrder2);
      });

      it('should accept null and undefined values', () => {
        const newOrder: INewOrder = { id: 'ABC' };
        expectedResult = service.addNewOrderToCollectionIfMissing([], null, newOrder, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(newOrder);
      });

      it('should return initial array if no NewOrder is added', () => {
        const newOrderCollection: INewOrder[] = [{ id: 'ABC' }];
        expectedResult = service.addNewOrderToCollectionIfMissing(newOrderCollection, undefined, null);
        expect(expectedResult).toEqual(newOrderCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
