import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AppAdService } from '../service/app-ad.service';
import { IAppAd, AppAd } from '../app-ad.model';
import { IAdmin } from 'app/entities/admin/admin.model';
import { AdminService } from 'app/entities/admin/service/admin.service';

import { AppAdUpdateComponent } from './app-ad-update.component';

describe('AppAd Management Update Component', () => {
  let comp: AppAdUpdateComponent;
  let fixture: ComponentFixture<AppAdUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appAdService: AppAdService;
  let adminService: AdminService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AppAdUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AppAdUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppAdUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appAdService = TestBed.inject(AppAdService);
    adminService = TestBed.inject(AdminService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Admin query and add missing value', () => {
      const appAd: IAppAd = { id: 'CBA' };
      const admin: IAdmin = { id: '0687c95b-dc08-4cea-a764-9eb1f0a9aea7' };
      appAd.admin = admin;

      const adminCollection: IAdmin[] = [{ id: 'd90a51b1-496c-4b7c-9d2b-166bc873e640' }];
      jest.spyOn(adminService, 'query').mockReturnValue(of(new HttpResponse({ body: adminCollection })));
      const additionalAdmins = [admin];
      const expectedCollection: IAdmin[] = [...additionalAdmins, ...adminCollection];
      jest.spyOn(adminService, 'addAdminToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appAd });
      comp.ngOnInit();

      expect(adminService.query).toHaveBeenCalled();
      expect(adminService.addAdminToCollectionIfMissing).toHaveBeenCalledWith(adminCollection, ...additionalAdmins);
      expect(comp.adminsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const appAd: IAppAd = { id: 'CBA' };
      const admin: IAdmin = { id: '09db7ad6-ff6f-43c4-9e2e-e17e48c5e424' };
      appAd.admin = admin;

      activatedRoute.data = of({ appAd });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(appAd));
      expect(comp.adminsSharedCollection).toContain(admin);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppAd>>();
      const appAd = { id: 'ABC' };
      jest.spyOn(appAdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appAd }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(appAdService.update).toHaveBeenCalledWith(appAd);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppAd>>();
      const appAd = new AppAd();
      jest.spyOn(appAdService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appAd }));
      saveSubject.complete();

      // THEN
      expect(appAdService.create).toHaveBeenCalledWith(appAd);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppAd>>();
      const appAd = { id: 'ABC' };
      jest.spyOn(appAdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appAdService.update).toHaveBeenCalledWith(appAd);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackAdminById', () => {
      it('Should return tracked Admin primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackAdminById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
