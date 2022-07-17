import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PermissionCompositeService } from '../service/permission-composite.service';

import { PermissionCompositeComponent } from './permission-composite.component';

describe('PermissionComposite Management Component', () => {
  let comp: PermissionCompositeComponent;
  let fixture: ComponentFixture<PermissionCompositeComponent>;
  let service: PermissionCompositeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'permission-composite', component: PermissionCompositeComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [PermissionCompositeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(PermissionCompositeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PermissionCompositeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PermissionCompositeService);

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
    expect(comp.permissionComposites?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
