import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EmployeeService } from '../service/employee.service';

import { EmployeeComponent } from './employee.component';

describe('Employee Management Component', () => {
  let comp: EmployeeComponent;
  let fixture: ComponentFixture<EmployeeComponent>;
  let service: EmployeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'employee', component: EmployeeComponent }]), HttpClientTestingModule],
      declarations: [EmployeeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(EmployeeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmployeeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(EmployeeService);

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
    expect(comp.employees?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
