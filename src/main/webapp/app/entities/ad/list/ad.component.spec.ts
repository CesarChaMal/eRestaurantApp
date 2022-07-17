import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AdService } from '../service/ad.service';

import { AdComponent } from './ad.component';

describe('Ad Management Component', () => {
  let comp: AdComponent;
  let fixture: ComponentFixture<AdComponent>;
  let service: AdService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'ad', component: AdComponent }]), HttpClientTestingModule],
      declarations: [AdComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(AdComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AdService);

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
    expect(comp.ads?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
