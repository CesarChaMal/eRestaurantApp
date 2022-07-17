import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAppAd, AppAd } from '../app-ad.model';

import { AppAdService } from './app-ad.service';

describe('AppAd Service', () => {
  let service: AppAdService;
  let httpMock: HttpTestingController;
  let elemDefault: IAppAd;
  let expectedResult: IAppAd | IAppAd[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AppAdService);
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

    it('should create a AppAd', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new AppAd()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AppAd', () => {
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

    it('should partial update a AppAd', () => {
      const patchObject = Object.assign({}, new AppAd());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AppAd', () => {
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

    it('should delete a AppAd', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAppAdToCollectionIfMissing', () => {
      it('should add a AppAd to an empty array', () => {
        const appAd: IAppAd = { id: 'ABC' };
        expectedResult = service.addAppAdToCollectionIfMissing([], appAd);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appAd);
      });

      it('should not add a AppAd to an array that contains it', () => {
        const appAd: IAppAd = { id: 'ABC' };
        const appAdCollection: IAppAd[] = [
          {
            ...appAd,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addAppAdToCollectionIfMissing(appAdCollection, appAd);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AppAd to an array that doesn't contain it", () => {
        const appAd: IAppAd = { id: 'ABC' };
        const appAdCollection: IAppAd[] = [{ id: 'CBA' }];
        expectedResult = service.addAppAdToCollectionIfMissing(appAdCollection, appAd);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appAd);
      });

      it('should add only unique AppAd to an array', () => {
        const appAdArray: IAppAd[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '6d62c434-8ec1-44c6-a4cd-70502ff4247c' }];
        const appAdCollection: IAppAd[] = [{ id: 'ABC' }];
        expectedResult = service.addAppAdToCollectionIfMissing(appAdCollection, ...appAdArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const appAd: IAppAd = { id: 'ABC' };
        const appAd2: IAppAd = { id: 'CBA' };
        expectedResult = service.addAppAdToCollectionIfMissing([], appAd, appAd2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(appAd);
        expect(expectedResult).toContain(appAd2);
      });

      it('should accept null and undefined values', () => {
        const appAd: IAppAd = { id: 'ABC' };
        expectedResult = service.addAppAdToCollectionIfMissing([], null, appAd, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(appAd);
      });

      it('should return initial array if no AppAd is added', () => {
        const appAdCollection: IAppAd[] = [{ id: 'ABC' }];
        expectedResult = service.addAppAdToCollectionIfMissing(appAdCollection, undefined, null);
        expect(expectedResult).toEqual(appAdCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
