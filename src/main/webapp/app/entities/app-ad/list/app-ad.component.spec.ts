import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AppAdService } from '../service/app-ad.service';

import { AppAdComponent } from './app-ad.component';

describe('AppAd Management Component', () => {
  let comp: AppAdComponent;
  let fixture: ComponentFixture<AppAdComponent>;
  let service: AppAdService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'app-ad', component: AppAdComponent }]), HttpClientTestingModule],
      declarations: [AppAdComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(AppAdComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppAdComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AppAdService);

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
    expect(comp.appAds?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
