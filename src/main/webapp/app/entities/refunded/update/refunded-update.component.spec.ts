import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RefundedService } from '../service/refunded.service';
import { IRefunded, Refunded } from '../refunded.model';

import { RefundedUpdateComponent } from './refunded-update.component';

describe('Refunded Management Update Component', () => {
  let comp: RefundedUpdateComponent;
  let fixture: ComponentFixture<RefundedUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let refundedService: RefundedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RefundedUpdateComponent],
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
      .overrideTemplate(RefundedUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RefundedUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    refundedService = TestBed.inject(RefundedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const refunded: IRefunded = { id: 'CBA' };

      activatedRoute.data = of({ refunded });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(refunded));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Refunded>>();
      const refunded = { id: 'ABC' };
      jest.spyOn(refundedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refunded });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: refunded }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(refundedService.update).toHaveBeenCalledWith(refunded);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Refunded>>();
      const refunded = new Refunded();
      jest.spyOn(refundedService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refunded });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: refunded }));
      saveSubject.complete();

      // THEN
      expect(refundedService.create).toHaveBeenCalledWith(refunded);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Refunded>>();
      const refunded = { id: 'ABC' };
      jest.spyOn(refundedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ refunded });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(refundedService.update).toHaveBeenCalledWith(refunded);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
