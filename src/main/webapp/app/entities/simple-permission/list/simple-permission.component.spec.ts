import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SimplePermissionService } from '../service/simple-permission.service';

import { SimplePermissionComponent } from './simple-permission.component';

describe('SimplePermission Management Component', () => {
  let comp: SimplePermissionComponent;
  let fixture: ComponentFixture<SimplePermissionComponent>;
  let service: SimplePermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'simple-permission', component: SimplePermissionComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [SimplePermissionComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(SimplePermissionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SimplePermissionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SimplePermissionService);

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
    expect(comp.simplePermissions?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
