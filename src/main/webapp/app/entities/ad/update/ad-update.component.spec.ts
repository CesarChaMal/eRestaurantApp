import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AdService } from '../service/ad.service';
import { IAd, Ad } from '../ad.model';

import { AdUpdateComponent } from './ad-update.component';

describe('Ad Management Update Component', () => {
  let comp: AdUpdateComponent;
  let fixture: ComponentFixture<AdUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let adService: AdService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AdUpdateComponent],
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
      .overrideTemplate(AdUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    adService = TestBed.inject(AdService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ad: IAd = { id: 'CBA' };

      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ad));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = { id: 'ABC' };
      jest.spyOn(adService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ad }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(adService.update).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = new Ad();
      jest.spyOn(adService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ad }));
      saveSubject.complete();

      // THEN
      expect(adService.create).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ad>>();
      const ad = { id: 'ABC' };
      jest.spyOn(adService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ad });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(adService.update).toHaveBeenCalledWith(ad);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
