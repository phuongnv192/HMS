import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ApiService } from 'src/app/services/api.service';
import { AuthService } from 'src/app/services/auth.service';
import { DialogService } from 'src/app/services/dialog.service';
import { BookingDetailDialog } from './booking-detail-dialog/booking-detail-dialog';

export interface BookingDetailNoteData {
  id: any;
  data: any;
  dateList: any;
  day: any;
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

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    public dialogRef: MatDialogRef<BookingDetailDialog>
  ) { }

  ngOnInit() {
    this.dateSchedule = [
      {
        id: 's1',
        name: 'Đơn dịch vụ #1',
        workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
        startTime: '8:30 AM',
        endTime: '10:30 AM',
        duration: '2',
        serviceAddOnIds: []
      },
      {
        id: 's2',
        name: 'Đơn dịch vụ #2',
        workDate: ["11/20/2023", "11/27/2023", "12/04/2023", "12/11/2023"],
        startTime: '8:30 AM',
        endTime: '10:30 AM',
        duration: '2',
        serviceAddOnIds: []
      },
      {
        id: 's3',
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
    this.data = {
      "ratingOverview": {
        "cleanerId": 1,
        "name": "Nguyen Hoang Anh",
        "idCard": "0123456789",
        "email": "abc@gmail.com",
        "phoneNumber": 84966069299,
        "status": "active",
        "branch": 1,
        "activityYear": 0,
        "averageRating": 5,
        "ratingNumber": 2
      },
      "history": [
        {
          "name": "Booking guest name 1",
          "ratingScore": 5,
          "workDate": "13/11/2023",
          "houseType": "APARTMENT",
          "floorNumber": 12,
          "floorArea": 120.0,
          "review": "Làm việc tích cực, nhanh gọn và sạch sẽ. Thái độ chuyên nghiệp và tỉ mỉ. Rất hài lòng."
        },
        {
          "name": "Booking guest name 2",
          "ratingScore": 4.5,
          "workDate": "13/12/2023",
          "houseType": "VILLA",
          "floorNumber": 12,
          "floorArea": 120.0,
          "review": "Khá là tuyệt. Nhanh nhẹn và thân thiện, dịch vụ chất lượng cao."
        }
      ]
    }
    this.schedule = this.data.history;
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

  viewDetailinSchedule(id: any, showtime: string) {
    // this.dialogService.sendDataDialog(true);
    console.log('document.body:', document.body); // Kiểm tra xem document.body có tồn tại hay không
    // if (this.dialogRef) {
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(BookingDetailDialog, {
      width: '600px',
      maxHeight: '80%',
      data: {
        id: id,
        detail: this.data,
        dateList: this.dateList,
        day: showtime
      },
      panelClass: ['view-detail']
    });
    console.log('this.dialogRef', this.dialogRef);

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed');
        this.renderer.removeClass(document.body, 'modal-open');
        // this.dialogService.sendDataDialog(false);
      }
    });
    // }
  }

}
