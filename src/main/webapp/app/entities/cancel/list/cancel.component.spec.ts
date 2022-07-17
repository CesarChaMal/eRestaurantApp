import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CancelService } from '../service/cancel.service';

import { CancelComponent } from './cancel.component';

describe('Cancel Management Component', () => {
  let comp: CancelComponent;
  let fixture: ComponentFixture<CancelComponent>;
  let service: CancelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'cancel', component: CancelComponent }]), HttpClientTestingModule],
      declarations: [CancelComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(CancelComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CancelComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CancelService);

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
    expect(comp.cancels?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
