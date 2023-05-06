import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {MatAutocompleteSelectedEvent} from '@angular/material/autocomplete';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { CertService } from 'src/app/services/cert-service.service';
import { CertifRequestDTO } from 'src/app/models/models';
import { LoginAuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-create-request',
  templateUrl: './create-request.component.html',
  styleUrls: ['./create-request.component.css']
})
export class CreateRequestComponent implements OnInit {

  // certifSNs: string[] = []
  // filteredSNs: Observable<string[]>
  // @ViewChild('SNInput') SNInput: ElementRef<HTMLInputElement> | undefined
  // SNCtrl = new FormControl('');
  selectedSN: string = '';

  adminLoggedIn: boolean = false
  selectedTypeToggleVal: string = '';
  selectedShortTermToggleVal: string = 'END';
  selectedDateVal: any;

  constructor(
    private certService: CertService,
    private http: HttpClient,
    private authService: LoginAuthService
    ) {
    // this.getAllSNs()
    // this.filteredSNs = this.SNCtrl.valueChanges.pipe(
    //   startWith(null),
    //   map((c: string | null) => (c ? this._filterCurr(c) : this.certifSNs.slice())),
    //   );
    }
    
  ngOnInit(): void {
  }

  // selectedSNFunc(event: MatAutocompleteSelectedEvent): void {
  //   if (this.selectedSN !== event.option.viewValue) this.selectedSN = event.option.viewValue
  //   if (this.SNInput != undefined) this.SNInput.nativeElement.value = ''
  //   this.SNCtrl.setValue(null);
  // }

  // private _filterCurr(value: string): string[] {
  //   const filterValue = value.toLowerCase();

  //   return this.certifSNs.filter(c => c.toLowerCase().includes(filterValue));
  // }

  // private getAllSNs() {
  //   this.http.get(environment.apiHost + 'certificate/allSN', {responseType: 'text'}).subscribe((res:any) => {
  //     this.certifSNs = res
  //     this.filteredSNs = this.SNCtrl.valueChanges.pipe(
  //       startWith(null),
  //       map((s: string | null) => (s ? this._filterCurr(s) : this.certifSNs.slice())),
  //     );
  //     console.log(this.certifSNs)
  //   })
  // }

  public onTypeToggleValChange(val: string) {
    this.selectedTypeToggleVal = val;
  }

  public onShortTermToggleValChange(val: string) {
    this.selectedTypeToggleVal = val;
  }

  public onRequestClick() {
    const req = {
      type: this.selectedTypeToggleVal,
      issuerSN: this.selectedSN,
      requesterEmail: this.authService.getEmail(),
      expirationTime: this.selectedDateVal
    }
    this.certService.sendRequest(req).subscribe(res => {
      alert(res)
    })
  }

}
