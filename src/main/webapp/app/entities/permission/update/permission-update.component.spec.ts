import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PermissionService } from '../service/permission.service';
import { IPermission, Permission } from '../permission.model';

import { PermissionUpdateComponent } from './permission-update.component';

describe('Permission Management Update Component', () => {
  let comp: PermissionUpdateComponent;
  let fixture: ComponentFixture<PermissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PermissionUpdateComponent],
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
      .overrideTemplate(PermissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PermissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    permissionService = TestBed.inject(PermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const permission: IPermission = { id: 'CBA' };

      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(permission));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Permission>>();
      const permission = { id: 'ABC' };
      jest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permission }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(permissionService.update).toHaveBeenCalledWith(permission);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Permission>>();
      const permission = new Permission();
      jest.spyOn(permissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permission }));
      saveSubject.complete();

      // THEN
      expect(permissionService.create).toHaveBeenCalledWith(permission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Permission>>();
      const permission = { id: 'ABC' };
      jest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(permissionService.update).toHaveBeenCalledWith(permission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
