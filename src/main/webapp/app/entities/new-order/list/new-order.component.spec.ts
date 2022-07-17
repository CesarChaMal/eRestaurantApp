import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { NewOrderService } from '../service/new-order.service';

import { NewOrderComponent } from './new-order.component';

describe('NewOrder Management Component', () => {
  let comp: NewOrderComponent;
  let fixture: ComponentFixture<NewOrderComponent>;
  let service: NewOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'new-order', component: NewOrderComponent }]), HttpClientTestingModule],
      declarations: [NewOrderComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(NewOrderComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NewOrderComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NewOrderService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.newOrders?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
