import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CompleteService } from '../service/complete.service';
import { IComplete, Complete } from '../complete.model';

import { CompleteUpdateComponent } from './complete-update.component';

describe('Complete Management Update Component', () => {
  let comp: CompleteUpdateComponent;
  let fixture: ComponentFixture<CompleteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let completeService: CompleteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CompleteUpdateComponent],
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
      .overrideTemplate(CompleteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CompleteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    completeService = TestBed.inject(CompleteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const complete: IComplete = { id: 'CBA' };

      activatedRoute.data = of({ complete });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(complete));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Complete>>();
      const complete = { id: 'ABC' };
      jest.spyOn(completeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complete });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: complete }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(completeService.update).toHaveBeenCalledWith(complete);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Complete>>();
      const complete = new Complete();
      jest.spyOn(completeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complete });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: complete }));
      saveSubject.complete();

      // THEN
      expect(completeService.create).toHaveBeenCalledWith(complete);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Complete>>();
      const complete = { id: 'ABC' };
      jest.spyOn(completeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ complete });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(completeService.update).toHaveBeenCalledWith(complete);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
