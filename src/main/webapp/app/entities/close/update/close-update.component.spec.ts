import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CloseService } from '../service/close.service';
import { IClose, Close } from '../close.model';

import { CloseUpdateComponent } from './close-update.component';

describe('Close Management Update Component', () => {
  let comp: CloseUpdateComponent;
  let fixture: ComponentFixture<CloseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let closeService: CloseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CloseUpdateComponent],
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
      .overrideTemplate(CloseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CloseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    closeService = TestBed.inject(CloseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const close: IClose = { id: 'CBA' };

      activatedRoute.data = of({ close });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(close));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Close>>();
      const close = { id: 'ABC' };
      jest.spyOn(closeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ close });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: close }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(closeService.update).toHaveBeenCalledWith(close);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Close>>();
      const close = new Close();
      jest.spyOn(closeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ close });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: close }));
      saveSubject.complete();

      // THEN
      expect(closeService.create).toHaveBeenCalledWith(close);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Close>>();
      const close = { id: 'ABC' };
      jest.spyOn(closeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ close });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(closeService.update).toHaveBeenCalledWith(close);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
