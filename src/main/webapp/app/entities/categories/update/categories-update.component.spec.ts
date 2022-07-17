import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CategoriesService } from '../service/categories.service';
import { ICategories, Categories } from '../categories.model';

import { CategoriesUpdateComponent } from './categories-update.component';

describe('Categories Management Update Component', () => {
  let comp: CategoriesUpdateComponent;
  let fixture: ComponentFixture<CategoriesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let categoriesService: CategoriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CategoriesUpdateComponent],
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
      .overrideTemplate(CategoriesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CategoriesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoriesService = TestBed.inject(CategoriesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const categories: ICategories = { id: 'CBA' };

      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(categories));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Categories>>();
      const categories = { id: 'ABC' };
      jest.spyOn(categoriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: categories }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(categoriesService.update).toHaveBeenCalledWith(categories);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Categories>>();
      const categories = new Categories();
      jest.spyOn(categoriesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: categories }));
      saveSubject.complete();

      // THEN
      expect(categoriesService.create).toHaveBeenCalledWith(categories);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Categories>>();
      const categories = { id: 'ABC' };
      jest.spyOn(categoriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categoriesService.update).toHaveBeenCalledWith(categories);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
