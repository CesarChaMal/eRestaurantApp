import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppDiscount, AppDiscount } from '../app-discount.model';

import { AppDiscountService } from './app-discount.service';

describe('AppDiscount Service', () => {
  let service: AppDiscountService;
  let httpMock: HttpTestingController;
  let elemDefault: IAppDiscount;
  let expectedResult: IAppDiscount | IAppDiscount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AppDiscountService);
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

    it('should create a AppDiscount', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AppDiscount()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AppDiscount', () => {
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

    it('should partial update a AppDiscount', () => {
      const patchObject = Object.assign(
        {
          code: 'BBBBBB',
          description: 'BBBBBB',
        },
        new AppDiscount()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AppDiscount', () => {
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

    it('should delete a AppDiscount', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAppDiscountToCollectionIfMissing', () => {
      it('should add a AppDiscount to an empty array', () => {
        const appDiscount: IAppDiscount = { id: 'ABC' };
        expectedResult = service.addAppDiscountToCollectionIfMissing([], appDiscount);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appDiscount);
      });

      it('should not add a AppDiscount to an array that contains it', () => {
        const appDiscount: IAppDiscount = { id: 'ABC' };
        const appDiscountCollection: IAppDiscount[] = [
          {
            ...appDiscount,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addAppDiscountToCollectionIfMissing(appDiscountCollection, appDiscount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AppDiscount to an array that doesn't contain it", () => {
        const appDiscount: IAppDiscount = { id: 'ABC' };
        const appDiscountCollection: IAppDiscount[] = [{ id: 'CBA' }];
        expectedResult = service.addAppDiscountToCollectionIfMissing(appDiscountCollection, appDiscount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appDiscount);
      });

      it('should add only unique AppDiscount to an array', () => {
        const appDiscountArray: IAppDiscount[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '98596dbe-6faf-459b-9bbb-0ad693416bbb' }];
        const appDiscountCollection: IAppDiscount[] = [{ id: 'ABC' }];
        expectedResult = service.addAppDiscountToCollectionIfMissing(appDiscountCollection, ...appDiscountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appDiscount: IAppDiscount = { id: 'ABC' };
        const appDiscount2: IAppDiscount = { id: 'CBA' };
        expectedResult = service.addAppDiscountToCollectionIfMissing([], appDiscount, appDiscount2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appDiscount);
        expect(expectedResult).toContain(appDiscount2);
      });

      it('should accept null and undefined values', () => {
        const appDiscount: IAppDiscount = { id: 'ABC' };
        expectedResult = service.addAppDiscountToCollectionIfMissing([], null, appDiscount, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appDiscount);
      });

      it('should return initial array if no AppDiscount is added', () => {
        const appDiscountCollection: IAppDiscount[] = [{ id: 'ABC' }];
        expectedResult = service.addAppDiscountToCollectionIfMissing(appDiscountCollection, undefined, null);
        expect(expectedResult).toEqual(appDiscountCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
