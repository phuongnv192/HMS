import { Subscription } from 'rxjs';
import { CalendarNoteData } from '../booking.component';
import { OnInit } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-calendar-dialog',
  templateUrl: './calendar-dialog.html',
  styleUrls: ['./calendar-dialog.css']
})

export class CalendarDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;


  constructor(
    public dialogRef: MatDialogRef<CalendarDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CalendarNoteData) {
  }

  ngOnInit(): void {

  }


  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }
}
