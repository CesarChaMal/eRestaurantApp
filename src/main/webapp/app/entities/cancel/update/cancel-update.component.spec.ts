import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CancelService } from '../service/cancel.service';
import { ICancel, Cancel } from '../cancel.model';

import { CancelUpdateComponent } from './cancel-update.component';

describe('Cancel Management Update Component', () => {
  let comp: CancelUpdateComponent;
  let fixture: ComponentFixture<CancelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cancelService: CancelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CancelUpdateComponent],
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
      .overrideTemplate(CancelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CancelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cancelService = TestBed.inject(CancelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const cancel: ICancel = { id: 'CBA' };

      activatedRoute.data = of({ cancel });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(cancel));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cancel>>();
      const cancel = { id: 'ABC' };
      jest.spyOn(cancelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cancel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cancel }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(cancelService.update).toHaveBeenCalledWith(cancel);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cancel>>();
      const cancel = new Cancel();
      jest.spyOn(cancelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cancel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cancel }));
      saveSubject.complete();

      // THEN
      expect(cancelService.create).toHaveBeenCalledWith(cancel);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Cancel>>();
      const cancel = { id: 'ABC' };
      jest.spyOn(cancelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cancel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cancelService.update).toHaveBeenCalledWith(cancel);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
