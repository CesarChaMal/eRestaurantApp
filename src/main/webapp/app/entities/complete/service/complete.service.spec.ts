import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IComplete, Complete } from '../complete.model';

import { CompleteService } from './complete.service';

describe('Complete Service', () => {
  let service: CompleteService;
  let httpMock: HttpTestingController;
  let elemDefault: IComplete;
  let expectedResult: IComplete | IComplete[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CompleteService);
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

    it('should create a Complete', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Complete()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Complete', () => {
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

    it('should partial update a Complete', () => {
      const patchObject = Object.assign(
        {
          enabled: true,
        },
        new Complete()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Complete', () => {
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

    it('should delete a Complete', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCompleteToCollectionIfMissing', () => {
      it('should add a Complete to an empty array', () => {
        const complete: IComplete = { id: 'ABC' };
        expectedResult = service.addCompleteToCollectionIfMissing([], complete);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(complete);
      });

      it('should not add a Complete to an array that contains it', () => {
        const complete: IComplete = { id: 'ABC' };
        const completeCollection: IComplete[] = [
          {
            ...complete,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCompleteToCollectionIfMissing(completeCollection, complete);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Complete to an array that doesn't contain it", () => {
        const complete: IComplete = { id: 'ABC' };
        const completeCollection: IComplete[] = [{ id: 'CBA' }];
        expectedResult = service.addCompleteToCollectionIfMissing(completeCollection, complete);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(complete);
      });

      it('should add only unique Complete to an array', () => {
        const completeArray: IComplete[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'f61d4f98-a854-42f5-a234-5ad24643d334' }];
        const completeCollection: IComplete[] = [{ id: 'ABC' }];
        expectedResult = service.addCompleteToCollectionIfMissing(completeCollection, ...completeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const complete: IComplete = { id: 'ABC' };
        const complete2: IComplete = { id: 'CBA' };
        expectedResult = service.addCompleteToCollectionIfMissing([], complete, complete2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(complete);
        expect(expectedResult).toContain(complete2);
      });

      it('should accept null and undefined values', () => {
        const complete: IComplete = { id: 'ABC' };
        expectedResult = service.addCompleteToCollectionIfMissing([], null, complete, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(complete);
      });

      it('should return initial array if no Complete is added', () => {
        const completeCollection: IComplete[] = [{ id: 'ABC' }];
        expectedResult = service.addCompleteToCollectionIfMissing(completeCollection, undefined, null);
        expect(expectedResult).toEqual(completeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
