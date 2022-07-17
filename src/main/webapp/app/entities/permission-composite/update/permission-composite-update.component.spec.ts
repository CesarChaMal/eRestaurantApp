import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PermissionCompositeService } from '../service/permission-composite.service';
import { IPermissionComposite, PermissionComposite } from '../permission-composite.model';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';

import { PermissionCompositeUpdateComponent } from './permission-composite-update.component';

describe('PermissionComposite Management Update Component', () => {
  let comp: PermissionCompositeUpdateComponent;
  let fixture: ComponentFixture<PermissionCompositeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let permissionCompositeService: PermissionCompositeService;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PermissionCompositeUpdateComponent],
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
      .overrideTemplate(PermissionCompositeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PermissionCompositeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    permissionCompositeService = TestBed.inject(PermissionCompositeService);
    permissionService = TestBed.inject(PermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call permission query and add missing value', () => {
      const permissionComposite: IPermissionComposite = { id: 'CBA' };
      const permission: IPermission = { id: 'cbfe3446-9d6b-40dc-b580-7638ea3624b8' };
      permissionComposite.permission = permission;

      const permissionCollection: IPermission[] = [{ id: '5516b24b-70d0-463a-9e9d-b932df5790fb' }];
      jest.spyOn(permissionService, 'query').mockReturnValue(of(new HttpResponse({ body: permissionCollection })));
      const expectedCollection: IPermission[] = [permission, ...permissionCollection];
      jest.spyOn(permissionService, 'addPermissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ permissionComposite });
      comp.ngOnInit();

      expect(permissionService.query).toHaveBeenCalled();
      expect(permissionService.addPermissionToCollectionIfMissing).toHaveBeenCalledWith(permissionCollection, permission);
      expect(comp.permissionsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const permissionComposite: IPermissionComposite = { id: 'CBA' };
      const permission: IPermission = { id: '8cc81db4-5848-4713-ab3e-e816de7bde2e' };
      permissionComposite.permission = permission;

      activatedRoute.data = of({ permissionComposite });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(permissionComposite));
      expect(comp.permissionsCollection).toContain(permission);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PermissionComposite>>();
      const permissionComposite = { id: 'ABC' };
      jest.spyOn(permissionCompositeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionComposite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permissionComposite }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(permissionCompositeService.update).toHaveBeenCalledWith(permissionComposite);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PermissionComposite>>();
      const permissionComposite = new PermissionComposite();
      jest.spyOn(permissionCompositeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionComposite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permissionComposite }));
      saveSubject.complete();

      // THEN
      expect(permissionCompositeService.create).toHaveBeenCalledWith(permissionComposite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<PermissionComposite>>();
      const permissionComposite = { id: 'ABC' };
      jest.spyOn(permissionCompositeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permissionComposite });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(permissionCompositeService.update).toHaveBeenCalledWith(permissionComposite);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackPermissionById', () => {
      it('Should return tracked Permission primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackPermissionById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
