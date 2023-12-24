import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ApiService } from 'src/app/services/api.service';
import { AuthService } from 'src/app/services/auth.service';
// import { DialogService } from 'src/app/services/dialog.service';
import { BookingDetailDialog } from './booking-detail-dialog/booking-detail-dialog';
import { BookingService } from 'src/app/services/booking.service';
import { HttpParams } from '@angular/common/http';
import { CleanerService } from 'src/app/services/cleaner.service';
import { CleanerBookingDetailDialog } from './cleaner-booking-detail-dialog/cleaner-booking-detail-dialog';

export interface CleanerBookingDetailNoteData {
  data:any,
  type: any,
}

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent implements OnInit {

  name = '';
  data: any;
  date: any;
  schedule: any;
  rateRange: string[];
  houseType: string[];
  searchRate: any;
  searchHouseType: string;
  dateList: any;
  dateSchedule: any[];
  bookingDetail: any;
  bookingList: any;
  apiBookingDetailExecuted = false;
  listBooking: any;
  listBookingSchedule = [];
  listBookingDay = [];
  size: number = 10;
  page: number = 0;

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    public dialogRef: MatDialogRef<CleanerBookingDetailDialog>,
    private bookingService: BookingService,
    private cleanService: CleanerService,
    private authService: AuthService,
  ) { }

  getListCleaner(id: any) {
    var req = new HttpParams()
      .set("page", this.page)
      .set("size", this.size);
    this.cleanService.getListSchedule(id, req).subscribe({
      next: (res) => {
        if (res && res.data) {
          this.listBooking = res.data.filter(a=> a.status != 'DONE');
          console.log("this.listBooking", this.listBooking);
          
          this.listBooking.forEach(booking => {
            if (
                booking.serviceTypeName == null &&
                booking.servicePackageName == null
            ) {
              console.log("listBookingDay");
              
              this.listBookingDay.push(booking);
            } else {
              this.listBookingSchedule.push(booking);
              console.log("listBookingDaySchedlue", this.listBookingSchedule);

            }
        });
        }
      },
      error: (error) => {},
    });
  }

  ngOnInit() {
    this.authService.getUserInfor().subscribe(data=> {
      this.cleanerId = data.data.id;
    this.bookingService.getBookingHistory(this.cleanerId).subscribe((data) => {
      this.data = data;
    });
    this.getListCleaner(this.cleanerId);

  });
    // this.bookingService.getListBooking().subscribe((data) => {
    //   this.bookingList = data.data;
    // });


    // this.data = {
    //   "ratingOverview": {
    //     "cleanerId": 1,
    //     "name": "Nguyen Hoang Anh",
    //     "idCard": "0123456789",
    //     "email": "abc@gmail.com",
    //     "phoneNumber": 84966069299,
    //     "status": "active",
    //     "branch": 1,
    //     "activityYear": 0,
    //     "averageRating": 5,
    //     "ratingNumber": 2
    //   },
    //   "history": [
    //     {
    //       "name": "Booking guest name 1",
    //       "ratingScore": 5,
    //       "workDate": "13/11/2023",
    //       "houseType": "APARTMENT",
    //       "floorNumber": 12,
    //       "floorArea": 120.0,
    //       "review": "Làm việc tích cực, nhanh gọn và sạch sẽ. Thái độ chuyên nghiệp và tỉ mỉ. Rất hài lòng."
    //     },
    //     {
    //       "name": "Booking guest name 2",
    //       "ratingScore": 4.5,
    //       "workDate": "13/12/2023",
    //       "houseType": "VILLA",
    //       "floorNumber": 12,
    //       "floorArea": 120.0,
    //       "review": "Khá là tuyệt. Nhanh nhẹn và thân thiện , dịch vụ chất lượng cao."
    //     }
    //   ]
    // }
    // this.schedule = this.data.history;
  }

  // onClickButton(id: any) {
  //   if (!this.apiBookingDetailExecuted) {
  //     // Gọi API abc ở đây
  //     this.bookingService.getBookingDetail().subscribe((data) => {
  //       this.apiBookingDetailExecuted = true;
  //       this.bookingDetail = data;
  //     });
  //   } else {
  //     // Nếu đã thực hiện API abc, thực hiện các tác vụ khác trực tiếp
  //     // this.performOtherTasks();
  //   }
  // }

  showDetail(detail: any, type:any) {
    // this.bookingService.getBookingDetail(detail).subscribe((data) => {
      // detail = data.data;
      
      this.renderer.addClass(document.body, 'modal-open');
      this.dialogRef = this.dialog.open(CleanerBookingDetailDialog, {
        width: '850px',
        height: '85%',
        data: {
          data: detail,
          type: type
        },
        panelClass: ['view-detail']
      });

      this.dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        this.renderer.removeClass(document.body, 'modal-open');
      });
    // });
  }

  search() {

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
  cleanerId(cleanerId: any) {
    throw new Error('Method not implemented.');
  }

  viewDetailinSchedule(id: any, showtime: string) {
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(CleanerBookingDetailDialog, {
      width: '900px',
      maxHeight: '60%',
      data: {
        id: id,
        detail: this.data,
        dateList: this.dateList,
        day: showtime
      },
      panelClass: ['view-detail']
    });

    this.dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
    });
  }

}