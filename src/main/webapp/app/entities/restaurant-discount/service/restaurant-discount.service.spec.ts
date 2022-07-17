import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRestaurantDiscount, RestaurantDiscount } from '../restaurant-discount.model';

import { RestaurantDiscountService } from './restaurant-discount.service';

describe('RestaurantDiscount Service', () => {
  let service: RestaurantDiscountService;
  let httpMock: HttpTestingController;
  let elemDefault: IRestaurantDiscount;
  let expectedResult: IRestaurantDiscount | IRestaurantDiscount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RestaurantDiscountService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      code: 'AAAAAAA',
      description: 'AAAAAAA',
      percentage: 0,
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

    it('should create a RestaurantDiscount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new RestaurantDiscount()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RestaurantDiscount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          code: 'BBBBBB',
          description: 'BBBBBB',
          percentage: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RestaurantDiscount', () => {
      const patchObject = Object.assign(
        {
          code: 'BBBBBB',
          description: 'BBBBBB',
          percentage: 1,
        },
        new RestaurantDiscount()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RestaurantDiscount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          code: 'BBBBBB',
          description: 'BBBBBB',
          percentage: 1,
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

    it('should delete a RestaurantDiscount', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRestaurantDiscountToCollectionIfMissing', () => {
      it('should add a RestaurantDiscount to an empty array', () => {
        const restaurantDiscount: IRestaurantDiscount = { id: 'ABC' };
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing([], restaurantDiscount);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantDiscount);
      });

      it('should not add a RestaurantDiscount to an array that contains it', () => {
        const restaurantDiscount: IRestaurantDiscount = { id: 'ABC' };
        const restaurantDiscountCollection: IRestaurantDiscount[] = [
          {
            ...restaurantDiscount,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing(restaurantDiscountCollection, restaurantDiscount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RestaurantDiscount to an array that doesn't contain it", () => {
        const restaurantDiscount: IRestaurantDiscount = { id: 'ABC' };
        const restaurantDiscountCollection: IRestaurantDiscount[] = [{ id: 'CBA' }];
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing(restaurantDiscountCollection, restaurantDiscount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantDiscount);
      });

      it('should add only unique RestaurantDiscount to an array', () => {
        const restaurantDiscountArray: IRestaurantDiscount[] = [
          { id: 'ABC' },
          { id: 'CBA' },
          { id: '0af44a8b-1a22-4753-906b-598d68791cf0' },
        ];
        const restaurantDiscountCollection: IRestaurantDiscount[] = [{ id: 'ABC' }];
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing(restaurantDiscountCollection, ...restaurantDiscountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const restaurantDiscount: IRestaurantDiscount = { id: 'ABC' };
        const restaurantDiscount2: IRestaurantDiscount = { id: 'CBA' };
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing([], restaurantDiscount, restaurantDiscount2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantDiscount);
        expect(expectedResult).toContain(restaurantDiscount2);
      });

      it('should accept null and undefined values', () => {
        const restaurantDiscount: IRestaurantDiscount = { id: 'ABC' };
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing([], null, restaurantDiscount, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantDiscount);
      });

      it('should return initial array if no RestaurantDiscount is added', () => {
        const restaurantDiscountCollection: IRestaurantDiscount[] = [{ id: 'ABC' }];
        expectedResult = service.addRestaurantDiscountToCollectionIfMissing(restaurantDiscountCollection, undefined, null);
        expect(expectedResult).toEqual(restaurantDiscountCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
