import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { LoginAuthService } from '../service/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PasswordReset } from 'src/app/models/models';

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.css']
})
export class ChangePasswordDialogComponent implements OnInit{
  oldPassword: string = ''
  code: string = ''
  newPassword: string = ''
  newPasswordRepeated: string = ''

  constructor (@Inject(MAT_DIALOG_DATA) public data: PasswordReset,
    public dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    private readonly authService: LoginAuthService,
    private readonly snackBar: MatSnackBar) { }

  ngOnInit (): void {
  }

  onCancelClick (): void {
    this.dialogRef.close()
  }

  onChangeClick (): void {
    if (this.newPassword !== this.newPasswordRepeated) {
      this.snackBar.open('New password and repeat password do not match', 'Close')
      return
    }
    let sender = this.authService.getEmail()
    if (this.data.type === 'phone') sender = this.authService.getPhone()
    this.authService.changePassword({email: this.authService.getEmail(), password: this.newPassword, phone: sender, code: this.code}).subscribe((res: any) => {     
      console.log(res)
      this.snackBar.open('Successful password change :)', 'Close')
    },
    (err: any) => {
      console.log(err)
      this.snackBar.open('Unsuccessful password change :(', 'Close')
    })
    
  }
}
