import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IState, State } from '../state.model';

import { StateService } from './state.service';

describe('State Service', () => {
  let service: StateService;
  let httpMock: HttpTestingController;
  let elemDefault: IState;
  let expectedResult: IState | IState[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StateService);
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

    it('should create a State', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new State()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a State', () => {
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

    it('should partial update a State', () => {
      const patchObject = Object.assign({}, new State());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of State', () => {
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

    it('should delete a State', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addStateToCollectionIfMissing', () => {
      it('should add a State to an empty array', () => {
        const state: IState = { id: 'ABC' };
        expectedResult = service.addStateToCollectionIfMissing([], state);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(state);
      });

      it('should not add a State to an array that contains it', () => {
        const state: IState = { id: 'ABC' };
        const stateCollection: IState[] = [
          {
            ...state,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, state);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a State to an array that doesn't contain it", () => {
        const state: IState = { id: 'ABC' };
        const stateCollection: IState[] = [{ id: 'CBA' }];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, state);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(state);
      });

      it('should add only unique State to an array', () => {
        const stateArray: IState[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '3edd0ba1-ed8c-406a-8ac8-3c4e2b4abb81' }];
        const stateCollection: IState[] = [{ id: 'ABC' }];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, ...stateArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const state: IState = { id: 'ABC' };
        const state2: IState = { id: 'CBA' };
        expectedResult = service.addStateToCollectionIfMissing([], state, state2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(state);
        expect(expectedResult).toContain(state2);
      });

      it('should accept null and undefined values', () => {
        const state: IState = { id: 'ABC' };
        expectedResult = service.addStateToCollectionIfMissing([], null, state, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(state);
      });

      it('should return initial array if no State is added', () => {
        const stateCollection: IState[] = [{ id: 'ABC' }];
        expectedResult = service.addStateToCollectionIfMissing(stateCollection, undefined, null);
        expect(expectedResult).toEqual(stateCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
