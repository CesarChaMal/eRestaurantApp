import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SimplePermissionService } from '../service/simple-permission.service';
import { ISimplePermission, SimplePermission } from '../simple-permission.model';

import { SimplePermissionUpdateComponent } from './simple-permission-update.component';

describe('SimplePermission Management Update Component', () => {
  let comp: SimplePermissionUpdateComponent;
  let fixture: ComponentFixture<SimplePermissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let simplePermissionService: SimplePermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SimplePermissionUpdateComponent],
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
      .overrideTemplate(SimplePermissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SimplePermissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    simplePermissionService = TestBed.inject(SimplePermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const simplePermission: ISimplePermission = { id: 'CBA' };

      activatedRoute.data = of({ simplePermission });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(simplePermission));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimplePermission>>();
      const simplePermission = { id: 'ABC' };
      jest.spyOn(simplePermissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simplePermission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: simplePermission }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(simplePermissionService.update).toHaveBeenCalledWith(simplePermission);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimplePermission>>();
      const simplePermission = new SimplePermission();
      jest.spyOn(simplePermissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simplePermission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: simplePermission }));
      saveSubject.complete();

      // THEN
      expect(simplePermissionService.create).toHaveBeenCalledWith(simplePermission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SimplePermission>>();
      const simplePermission = { id: 'ABC' };
      jest.spyOn(simplePermissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ simplePermission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(simplePermissionService.update).toHaveBeenCalledWith(simplePermission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
