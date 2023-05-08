import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateCertificateComponent } from './validate-certificate.component';

describe('ValidateCertificateComponent', () => {
  let component: ValidateCertificateComponent;
  let fixture: ComponentFixture<ValidateCertificateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValidateCertificateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValidateCertificateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
