import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OnHoldService } from '../service/on-hold.service';
import { IOnHold, OnHold } from '../on-hold.model';

import { OnHoldUpdateComponent } from './on-hold-update.component';

describe('OnHold Management Update Component', () => {
  let comp: OnHoldUpdateComponent;
  let fixture: ComponentFixture<OnHoldUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let onHoldService: OnHoldService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OnHoldUpdateComponent],
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
      .overrideTemplate(OnHoldUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OnHoldUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    onHoldService = TestBed.inject(OnHoldService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const onHold: IOnHold = { id: 'CBA' };

      activatedRoute.data = of({ onHold });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(onHold));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OnHold>>();
      const onHold = { id: 'ABC' };
      jest.spyOn(onHoldService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ onHold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: onHold }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(onHoldService.update).toHaveBeenCalledWith(onHold);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OnHold>>();
      const onHold = new OnHold();
      jest.spyOn(onHoldService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ onHold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: onHold }));
      saveSubject.complete();

      // THEN
      expect(onHoldService.create).toHaveBeenCalledWith(onHold);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OnHold>>();
      const onHold = { id: 'ABC' };
      jest.spyOn(onHoldService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ onHold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(onHoldService.update).toHaveBeenCalledWith(onHold);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
