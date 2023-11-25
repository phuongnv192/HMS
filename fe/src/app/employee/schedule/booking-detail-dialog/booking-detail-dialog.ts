import { Subscription } from 'rxjs';
import { BookingDetailNoteData } from '../schedule.component';
import { OnInit } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-booking-detail-dialog',
  templateUrl: './booking-detail-dialog.html',
  styleUrls: ['./booking-detail-dialog.css']
})

export class BookingDetailDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;


  constructor(
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BookingDetailNoteData) {
  }

  ngOnInit(): void {

  }


  onNoClick(): void {
    if (this.dialogRef) {
      console.log('Đóng dialog');
      this.dialogRef.close();
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }
}