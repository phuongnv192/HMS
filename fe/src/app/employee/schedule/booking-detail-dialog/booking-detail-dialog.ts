import { Subscription } from 'rxjs';
import { Component, Inject, OnDestroy, NgZone, Renderer2, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';
import { BookingDetailNoteData } from 'src/app/customer/customer-booking/customer-booking-list/customer-schedule.component';

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
  public id: any;
  detail: any;
  scheduleDay: any;
  scheduleDayTime: any;
  scheduleStartTime: any;
  scheduleEndTime: any;
  schedulePaymentStatus: any;
  schedulePaymentNote: any;
  scheduleAddOns: any;
  namesOfScheduleDay: any;
  scheduleDayList: any;
  areaTypes: any;
  cleanerNum: any;
  duration: any;
  priceClean: any;
  formattedPrice: any;
  infor: any;


  constructor(
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BookingDetailNoteData,
    private bookingServicee: BookingService,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }

  ngOnInit(): void {
    this.data.data.forEach(element => {
      if(element.id == this.data.id){
        this.infor = element;
      }
    });
    this.bookingServicee.getDataService().subscribe(res => {
      this.areaTypes = res.data;
      this.onAreaChange(this.data.data.floorArea);
    });

    // this.detail = this.data.detail;
    // }

    // this.dateSchedule = [
    //   {
    //     id: '1',
    //     workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
    //   },
    //   {
    //     id: '2',
    //     workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
    //   },
    //   {
    //     id: '3',
    //     workDate: ["11/20/2023", "11/21/2023", "11/22/2023", "11/23/2023", "11/24/2023", "11/25/2023", "11/26/2023", "11/27/2023", "11/28/2023"],
    //   }
    // ]
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


    // this.scheduleDay = this.data.detail.scheduleList[0];

    // .workDate;
    // this.scheduleStartTime = this.data.detail.scheduleList[0].startTime;
    // this.scheduleEndTime = this.data.detail.scheduleList[0].endTime;
    // this.schedulePaymentStatus = this.data.detail.scheduleList[0].paymentStatus;
    // this.schedulePaymentNote = this.data.detail.scheduleList[0].paymentNote;
    // this.scheduleAddOns = this.data.detail.scheduleList[0].serviceAddOns;
    this.namesOfScheduleDay = this.scheduleAddOns.map(item => item.name).join(', ');
    this.scheduleDayList = this.detail.scheduleList.map(item => item.workDate);

    this.dateSchedule = [{
      id: this.id,
      workDate: this.scheduleDayList
    }];
    // this.date = this.dateSchedule.find((item) => item.id == this.data.id);
  }

  onAreaChange(value: any) {
    // let found = false; // Biến kiểm soát vòng lặp
    this.areaTypes.forEach(element => {
      if (value == element.key) {
        // Nếu tìm thấy phần tử thỏa mãn điều kiện, lưu giá trị và thoát vòng lặp
        this.cleanerNum = element.cleanerNum;
        this.duration = element.duration;
        this.priceClean = element.price;
        this.formattedPrice = this.priceClean.toLocaleString('vi-VN', {
          style: 'currency',
          currency: 'VND',
        });

        // found = true;
      }
    });
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
    let day = today.getDate().toString().padStart(2, '0');
    return day;
  }

  getMonth(todayin) {
    let today = new Date(todayin);
    let month = (today.getMonth() + 1).toString().padStart(2, '0');
    return month;
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