import { Subscription } from 'rxjs';
import { Component, Inject, OnDestroy, NgZone, Renderer2, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';
import { BookingDetailNoteData } from 'src/app/customer/customer-booking/customer-booking-list/customer-schedule.component';
import { ScheduleDialog } from '../cleaner-booking-detail-dialog/schedule-dialog/schedule.dialog';

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
  type: any;
  cleanerList = [];
  dataCleaner = [];
  idCards: any[];
  listName: any[];
  rating = [];
  ratingAverage = 0;
  status = 'DONE';
  page = 0;
  size = 100;

  constructor(
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BookingDetailNoteData,
    private bookingServicee: BookingService,
    private renderer: Renderer2, public dialog: MatDialog,
    public scheduleDialogRef: MatDialogRef<ScheduleDialog>,
    private ngZone: NgZone) {
  }

  ngOnInit(): void {
    this.infor = this.data.data;    
    this.bookingServicee.getCustomerBookingHistory(this.page, this.size).subscribe(res => {
      if (res && res.data) {
        res.data.forEach(element_res => {
          if (element_res.id == this.infor.bookingId) {
            this.dataCleaner = element_res.cleaners;
            this.idCards = this.dataCleaner.map(cleaner => cleaner.idCard);
          }
        });
      }
    });
    this.status = this.infor.status;
    if (this.status == "DONE") {
      this.rating = this.infor.scheduleDayList.map(rate => (rate.ratingScore != null ? rate.ratingScore : 0));
      console.log("rating", this.rating);
    }

    const totalRating = this.rating.reduce((acc, current) => acc + current, 0);
    // Tính tổng số phần tử của mảng rating
    const numberOfElements = this.rating.length;
    // Tính rating trung bình
    this.ratingAverage = numberOfElements > 0 ? totalRating / numberOfElements : 0;
    this.type = this.data.data.servicePackageName != null ? 'schedule' : 'day';
    this.id = this.data.data.bookingId;
    this.formattedPrice = this.data.data.totalBookingPrice.toLocaleString('vi-VN', {
      style: 'currency',
      currency: 'VND',
    });
    this.bookingServicee.getDataService().subscribe(res => {
      this.areaTypes = res.data;
      this.onAreaChange(this.data.data.floorArea);
    });

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
    this.namesOfScheduleDay = this.scheduleAddOns.map(item => item.name).join(', ');
    this.scheduleDayList = this.data.data.scheduleList.map(item => item.workDate);

    this.dateSchedule = [{
      id: this.id,
      workDate: this.scheduleDayList
    }];
  }

  generateStarRating(rating: number): string {
    const stars: string[] = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;

    for (let i = 0; i < fullStars; i++) {
      stars.push('<i class="fas fa-star"></i>');
    }

    if (hasHalfStar) {
      stars.push('<i class="fas fa-star-half-alt"></i>');
    }

    return stars.join("");
  }


  confirm(id:any){
    this.renderer.addClass(document.body, 'modal-open');
    this.scheduleDialogRef = this.dialog.open(ScheduleDialog, {
      width: '800px',
      maxHeight: '80%',
      data: {
        data: this.infor.scheduleList,
        id: this.infor.bookingId,
      },
      panelClass: ['schedule-stauts']
    });

    this.scheduleDialogRef.afterClosed().subscribe(result => {
      if(result){
       
      }
      this.renderer.removeClass(document.body, 'modal-open');
    }); 
  }

  cancel(id:any){

  }

  onAreaChange(value: any) {
    // let found = false; // Biến kiểm soát vòng lặp
    this.areaTypes.forEach(element => {
      if (value == element.floorArea) {
        console.log(element);

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