import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefreshPasswordComponent } from './refresh-password.component';

describe('RefreshPasswordComponent', () => {
  let component: RefreshPasswordComponent;
  let fixture: ComponentFixture<RefreshPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RefreshPasswordComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RefreshPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
