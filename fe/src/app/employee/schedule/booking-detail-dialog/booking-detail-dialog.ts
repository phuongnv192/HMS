import { Subscription } from 'rxjs';
import { BookingDetailNoteData } from '../schedule.component';
import { Component, Inject, OnDestroy, NgZone, Renderer2, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-booking-detail-dialog',
  templateUrl: './booking-detail-dialog.html',
  styleUrls: ['./booking-detail-dialog.scss']
})

export class BookingDetailDialog implements OnInit, OnDestroy {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  dateList: any[];
  dateSchedule: any[];
  selectedDate: any;
  date: any;
  public name: any;


  constructor(
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BookingDetailNoteData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }

  ngOnInit(): void {
    console.log('BookingDetailDialog oninit');
    this.dateSchedule = [
      {
        id: '1',
        name: 'Đơn dịch vụ #1',
        workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
        startTime: '8:30 AM',
        endTime: '10:30 AM',
        duration: '2',
        serviceAddOnIds: []
      },
      {
        id: '2',
        name: 'Đơn dịch vụ #2',
        workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
        startTime: '8:30 AM',
        endTime: '10:30 AM',
        duration: '2',
        serviceAddOnIds: []
      },
      {
        id: '3',
        name: 'Đơn dịch vụ #3',
        workDate: ["11/20/2023", "11/21/2023", "11/22/2023", "11/23/2023", "11/24/2023", "11/25/2023", "11/26/2023", "11/27/2023", "11/28/2023"],
        startTime: '8:30 AM',
        endTime: '10:30 AM',
        duration: '2',
        serviceAddOnIds: []
      }
    ]
    this.dateList = [
      {
        id: 'd1',
        workDate: "12/20/2023",
        name: 'Đơn dịch vụ ngày #1',
        startTime: "08:00 AM",
        endTime: "11:00 AM",
        duration: 3
      },
      {
        id: 'd2',
        workDate: "12/21/2023",
        name: 'Đơn dịch vụ ngày #2',
        startTime: "09:00 AM",
        endTime: "10:00 AM",
        duration: 1
      },
      {
        id: 'd1',
        workDate: "12/22/2023",
        name: 'Đơn dịch vụ ngày #3',
        startTime: "03:00 PM",
        endTime: "05:00 AM",
        duration: 3
      },
    ]

    this.date = this.dateSchedule.find((item) => item.id == this.data.id);
    this.name = this.data.name;
    console.log("name", this.data);

  }


  onNoClick(): void {
    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close(true);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  getDay(todayin) {
    let today = new Date(todayin);
    return today.getDate();
  }

  getMonth(todayin) {
    let today = new Date(todayin);
    return today.getMonth() + 1;
  }

  getDateMonth(todayin) {
    let days = {
      Sunday: "CN",
      Monday: "Thứ 2",
      Tuesday: "Thứ 3",
      Wednesday: "Thứ 4",
      Thursday: "Thứ 5",
      Friday: "Thứ 6",
      Saturday: "Thứ 7"
    }
    let today = new Date(todayin);
    return days[today.toLocaleDateString('en-US', { weekday: 'long' })];
  }

  getYearDateMonth(todayin) {
    let today = new Date(todayin);
    return (today.getMonth() + 1) + '/' + today.getDate() + '/' + today.getFullYear();
  }

}