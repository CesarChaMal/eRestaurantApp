import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRestaurantAd, RestaurantAd } from '../restaurant-ad.model';

import { RestaurantAdService } from './restaurant-ad.service';

describe('RestaurantAd Service', () => {
  let service: RestaurantAdService;
  let httpMock: HttpTestingController;
  let elemDefault: IRestaurantAd;
  let expectedResult: IRestaurantAd | IRestaurantAd[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RestaurantAdService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      url: 'AAAAAAA',
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

    it('should create a RestaurantAd', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new RestaurantAd()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RestaurantAd', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should partial update a RestaurantAd', () => {
      const patchObject = Object.assign(
        {
          url: 'BBBBBB',
        },
        new RestaurantAd()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RestaurantAd', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          url: 'BBBBBB',
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

    it('should delete a RestaurantAd', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRestaurantAdToCollectionIfMissing', () => {
      it('should add a RestaurantAd to an empty array', () => {
        const restaurantAd: IRestaurantAd = { id: 'ABC' };
        expectedResult = service.addRestaurantAdToCollectionIfMissing([], restaurantAd);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantAd);
      });

      it('should not add a RestaurantAd to an array that contains it', () => {
        const restaurantAd: IRestaurantAd = { id: 'ABC' };
        const restaurantAdCollection: IRestaurantAd[] = [
          {
            ...restaurantAd,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addRestaurantAdToCollectionIfMissing(restaurantAdCollection, restaurantAd);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RestaurantAd to an array that doesn't contain it", () => {
        const restaurantAd: IRestaurantAd = { id: 'ABC' };
        const restaurantAdCollection: IRestaurantAd[] = [{ id: 'CBA' }];
        expectedResult = service.addRestaurantAdToCollectionIfMissing(restaurantAdCollection, restaurantAd);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantAd);
      });

      it('should add only unique RestaurantAd to an array', () => {
        const restaurantAdArray: IRestaurantAd[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'f89a1936-df8a-4975-b87c-c4184dc00d6d' }];
        const restaurantAdCollection: IRestaurantAd[] = [{ id: 'ABC' }];
        expectedResult = service.addRestaurantAdToCollectionIfMissing(restaurantAdCollection, ...restaurantAdArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const restaurantAd: IRestaurantAd = { id: 'ABC' };
        const restaurantAd2: IRestaurantAd = { id: 'CBA' };
        expectedResult = service.addRestaurantAdToCollectionIfMissing([], restaurantAd, restaurantAd2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantAd);
        expect(expectedResult).toContain(restaurantAd2);
      });

      it('should accept null and undefined values', () => {
        const restaurantAd: IRestaurantAd = { id: 'ABC' };
        expectedResult = service.addRestaurantAdToCollectionIfMissing([], null, restaurantAd, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantAd);
      });

      it('should return initial array if no RestaurantAd is added', () => {
        const restaurantAdCollection: IRestaurantAd[] = [{ id: 'ABC' }];
        expectedResult = service.addRestaurantAdToCollectionIfMissing(restaurantAdCollection, undefined, null);
        expect(expectedResult).toEqual(restaurantAdCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
