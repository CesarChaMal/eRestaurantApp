import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProfile, Profile } from '../profile.model';

import { ProfileService } from './profile.service';

describe('Profile Service', () => {
  let service: ProfileService;
  let httpMock: HttpTestingController;
  let elemDefault: IProfile;
  let expectedResult: IProfile | IProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileService);
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

    it('should create a Profile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Profile()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Profile', () => {
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

    it('should partial update a Profile', () => {
      const patchObject = Object.assign({}, new Profile());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Profile', () => {
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

    it('should delete a Profile', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProfileToCollectionIfMissing', () => {
      it('should add a Profile to an empty array', () => {
        const profile: IProfile = { id: 'ABC' };
        expectedResult = service.addProfileToCollectionIfMissing([], profile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profile);
      });

      it('should not add a Profile to an array that contains it', () => {
        const profile: IProfile = { id: 'ABC' };
        const profileCollection: IProfile[] = [
          {
            ...profile,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, profile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Profile to an array that doesn't contain it", () => {
        const profile: IProfile = { id: 'ABC' };
        const profileCollection: IProfile[] = [{ id: 'CBA' }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, profile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profile);
      });

      it('should add only unique Profile to an array', () => {
        const profileArray: IProfile[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '3b37d61d-4a56-4aa2-8e64-b7c60aaefa83' }];
        const profileCollection: IProfile[] = [{ id: 'ABC' }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, ...profileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profile: IProfile = { id: 'ABC' };
        const profile2: IProfile = { id: 'CBA' };
        expectedResult = service.addProfileToCollectionIfMissing([], profile, profile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profile);
        expect(expectedResult).toContain(profile2);
      });

      it('should accept null and undefined values', () => {
        const profile: IProfile = { id: 'ABC' };
        expectedResult = service.addProfileToCollectionIfMissing([], null, profile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profile);
      });

      it('should return initial array if no Profile is added', () => {
        const profileCollection: IProfile[] = [{ id: 'ABC' }];
        expectedResult = service.addProfileToCollectionIfMissing(profileCollection, undefined, null);
        expect(expectedResult).toEqual(profileCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
