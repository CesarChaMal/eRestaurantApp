import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICancel, Cancel } from '../cancel.model';

import { CancelService } from './cancel.service';

describe('Cancel Service', () => {
  let service: CancelService;
  let httpMock: HttpTestingController;
  let elemDefault: ICancel;
  let expectedResult: ICancel | ICancel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CancelService);
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

    it('should create a Cancel', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Cancel()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cancel', () => {
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

    it('should partial update a Cancel', () => {
      const patchObject = Object.assign(
        {
          enabled: true,
        },
        new Cancel()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cancel', () => {
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

    it('should delete a Cancel', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCancelToCollectionIfMissing', () => {
      it('should add a Cancel to an empty array', () => {
        const cancel: ICancel = { id: 'ABC' };
        expectedResult = service.addCancelToCollectionIfMissing([], cancel);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cancel);
      });

      it('should not add a Cancel to an array that contains it', () => {
        const cancel: ICancel = { id: 'ABC' };
        const cancelCollection: ICancel[] = [
          {
            ...cancel,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCancelToCollectionIfMissing(cancelCollection, cancel);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cancel to an array that doesn't contain it", () => {
        const cancel: ICancel = { id: 'ABC' };
        const cancelCollection: ICancel[] = [{ id: 'CBA' }];
        expectedResult = service.addCancelToCollectionIfMissing(cancelCollection, cancel);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cancel);
      });

      it('should add only unique Cancel to an array', () => {
        const cancelArray: ICancel[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'eafd9524-7021-437b-8b9a-e0a5ebe07d6f' }];
        const cancelCollection: ICancel[] = [{ id: 'ABC' }];
        expectedResult = service.addCancelToCollectionIfMissing(cancelCollection, ...cancelArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cancel: ICancel = { id: 'ABC' };
        const cancel2: ICancel = { id: 'CBA' };
        expectedResult = service.addCancelToCollectionIfMissing([], cancel, cancel2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cancel);
        expect(expectedResult).toContain(cancel2);
      });

      it('should accept null and undefined values', () => {
        const cancel: ICancel = { id: 'ABC' };
        expectedResult = service.addCancelToCollectionIfMissing([], null, cancel, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cancel);
      });

      it('should return initial array if no Cancel is added', () => {
        const cancelCollection: ICancel[] = [{ id: 'ABC' }];
        expectedResult = service.addCancelToCollectionIfMissing(cancelCollection, undefined, null);
        expect(expectedResult).toEqual(cancelCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
