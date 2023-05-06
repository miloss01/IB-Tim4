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
    private readonly loginAuthService: LoginAuthService,
    private readonly snackBar: MatSnackBar) { }

  ngOnInit (): void {
  }

  onCancelClick (): void {
    this.dialogRef.close()
  }

  requestCode (): void {
    if (this.newPassword !== this.newPasswordRepeated) {
      this.snackBar.open('New password and repeat password do not match', 'Close')
      return
    }
    
  }
}
