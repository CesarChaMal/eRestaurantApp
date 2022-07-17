import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClose, Close } from '../close.model';

import { CloseService } from './close.service';

describe('Close Service', () => {
  let service: CloseService;
  let httpMock: HttpTestingController;
  let elemDefault: IClose;
  let expectedResult: IClose | IClose[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CloseService);
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

    it('should create a Close', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Close()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Close', () => {
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

    it('should partial update a Close', () => {
      const patchObject = Object.assign(
        {
          enabled: true,
        },
        new Close()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Close', () => {
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

    it('should delete a Close', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCloseToCollectionIfMissing', () => {
      it('should add a Close to an empty array', () => {
        const close: IClose = { id: 'ABC' };
        expectedResult = service.addCloseToCollectionIfMissing([], close);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(close);
      });

      it('should not add a Close to an array that contains it', () => {
        const close: IClose = { id: 'ABC' };
        const closeCollection: IClose[] = [
          {
            ...close,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCloseToCollectionIfMissing(closeCollection, close);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Close to an array that doesn't contain it", () => {
        const close: IClose = { id: 'ABC' };
        const closeCollection: IClose[] = [{ id: 'CBA' }];
        expectedResult = service.addCloseToCollectionIfMissing(closeCollection, close);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(close);
      });

      it('should add only unique Close to an array', () => {
        const closeArray: IClose[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '527b6e4f-cdf3-418e-9d97-c50507e96a93' }];
        const closeCollection: IClose[] = [{ id: 'ABC' }];
        expectedResult = service.addCloseToCollectionIfMissing(closeCollection, ...closeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const close: IClose = { id: 'ABC' };
        const close2: IClose = { id: 'CBA' };
        expectedResult = service.addCloseToCollectionIfMissing([], close, close2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(close);
        expect(expectedResult).toContain(close2);
      });

      it('should accept null and undefined values', () => {
        const close: IClose = { id: 'ABC' };
        expectedResult = service.addCloseToCollectionIfMissing([], null, close, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(close);
      });

      it('should return initial array if no Close is added', () => {
        const closeCollection: IClose[] = [{ id: 'ABC' }];
        expectedResult = service.addCloseToCollectionIfMissing(closeCollection, undefined, null);
        expect(expectedResult).toEqual(closeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
