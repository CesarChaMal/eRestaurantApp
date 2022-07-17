jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { RestaurantDiscountService } from '../service/restaurant-discount.service';

import { RestaurantDiscountDeleteDialogComponent } from './restaurant-discount-delete-dialog.component';

describe('RestaurantDiscount Management Delete Component', () => {
  let comp: RestaurantDiscountDeleteDialogComponent;
  let fixture: ComponentFixture<RestaurantDiscountDeleteDialogComponent>;
  let service: RestaurantDiscountService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RestaurantDiscountDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(RestaurantDiscountDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RestaurantDiscountDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RestaurantDiscountService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete('ABC');
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith('ABC');
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
