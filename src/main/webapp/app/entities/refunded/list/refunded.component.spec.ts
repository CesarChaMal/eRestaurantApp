import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RefundedService } from '../service/refunded.service';

import { RefundedComponent } from './refunded.component';

describe('Refunded Management Component', () => {
  let comp: RefundedComponent;
  let fixture: ComponentFixture<RefundedComponent>;
  let service: RefundedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'refunded', component: RefundedComponent }]), HttpClientTestingModule],
      declarations: [RefundedComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(RefundedComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RefundedComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RefundedService);

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
    expect(comp.refundeds?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
