import { Subscription } from 'rxjs';
import { CalendarDialogData } from '../booking.component';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { addDays, addWeeks, format, addMonths } from 'date-fns';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-calendar-dialog',
  templateUrl: './calendar-dialog.html',
  styleUrls: ['./calendar-dialog.scss']
})

export class CalendarDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  dateList: any[];
  dateSchedule: any[];
  date: any;
  public name: any;
  public id: any;
  pickDay: any;
  scheduleDay: any;
  scheduleDayTime: any;
  scheduleStartTime: any;
  scheduleEndTime: any;
  schedulePaymentStatus: any;
  schedulePaymentNote: any;
  scheduleAddOns: any;
  namesOfScheduleDay: any;
  scheduleDayList: any;
  selectedServiceTypeId: string; // Biến lưu trữ loại dịch vụ sàn được chọn
  serviceTypes: any[] = [];
  selectedServicePackageId: number; // Biến lưu trữ loại dịch vụ sàn được chọn
  servicePackages: any[] = [];
  service: any;
  packagesTemp: any[] = [];
  pickServiceType: any;
  dateArray: any[] = [];
  selectedTimeType = true;
  selectedDate: NgbDateStruct;
  showDatePicker: boolean;
  datePicker: string;
  datePickerShow: any;
  scheduleDescription = '';

  constructor(
    public dialogRef: MatDialogRef<CalendarDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CalendarDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }

  getServiceTypeNames(): string[] {
    return this.data.type.map(data => data.serviceTypeName);
  }

  getId(serviceData, targetServicePackageName) {
    for (let i = 0; i < serviceData.length; i++) {
      const serviceType = serviceData[i];
      if (serviceType.serviceTypeName == targetServicePackageName) {
        return serviceType.serviceTypeId;
      }
    }

    // Trả về null nếu không tìm thấy
    return null;
  }

  ngOnInit(): void {
    this.pickDay = this.data.pickDay;

    if (this.pickDay) {

    } else {
      this.selectedServiceTypeId = this.data.type[0].serviceTypeName;
    }

    this.pickServiceType = this.getId(this.data.type, this.selectedServiceTypeId);

    this.data.type.forEach(element => {
      this.serviceTypes.push(element.serviceTypeName);
    });

    this.packagesTemp = this.data.type[this.pickServiceType - 1].servicePackages;

    this.packagesTemp.forEach(element => {
      this.servicePackages.push(element.servicePackageName + ' - ' + element.unit);
    });


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


    // this.scheduleDay = this.data.pickDay.scheduleList[0];

    // // .workDate;
    // // this.scheduleStartTime = this.data.pickDay.scheduleList[0].startTime;
    // // this.scheduleEndTime = this.data.pickDay.scheduleList[0].endTime;
    // // this.schedulePaymentStatus = this.data.pickDay.scheduleList[0].paymentStatus;
    // // this.schedulePaymentNote = this.data.pickDay.scheduleList[0].paymentNote;
    // this.scheduleAddOns = this.data.pickDay.scheduleList[0].serviceAddOns;
    // this.namesOfScheduleDay = this.scheduleAddOns.map(item => item.name).join(', ');
    // this.scheduleDayList = this.pickDay.scheduleList.map(item => item.workDate);
    // console.log("scheduleDayList", this.scheduleDayList);

    // this.dateSchedule = [{
    //   id: this.id,
    //   workDate: this.scheduleDayList
    // }];
    // this.date = this.dateSchedule.find((item) => item.id == this.data.id);

    console.log("dateSchedule", this.dateSchedule);
  }

  selectedService(event: any) {
    this.servicePackages = [];
    console.log("selectedServiceTypeId", this.selectedServiceTypeId);

    this.pickServiceType = this.getId(this.data.type, this.selectedServiceTypeId);

    this.packagesTemp = this.data.type[this.pickServiceType - 1].servicePackages;

    this.packagesTemp.forEach(element => {
      this.servicePackages.push(element.servicePackageName + ' - ' + element.unit);
    });

    if (this.pickServiceType == 1 || this.pickServiceType == 3) {
      this.selectedTimeType = true;
    }
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
    const inputDate = new Date(todayin); // Ngày tháng năm (MM-dd-yyyy)
    console.log('Tháng:', inputDate);

    const monthValue = inputDate.getMonth() + 1; // Thêm 1 để chuyển từ 0-11 thành 1-12

    // Định dạng giá trị tháng thành chuỗi
    let formattedMonth = monthValue.toString();
    // let today = new Date(todayin);
    // console.log("today", today);
    
    // let month = (today.getMonth() + 1).toString().padStart(2, '0');
    return formattedMonth;
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
    return today.getDate()  + '/' + (today.getMonth() + 1) + '/' + today.getFullYear();
  }


  getListDay(id: any, month:any, date: string) {
    this.dateArray = [];
    // Giả sử a là số ngày và b là đơn vị thời gian (tháng, ngày, năm)
    let a = 0;
    let b = '';
    // Ngày đầu vào
    let inputDate = new Date(date);
    if (id == 1) {
      a = 30;
      b = 'month';
      for (let i = 0; i < a; i++) {
        const currentDate = addDays(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
      console.log('Mảng ngày:', this.dateArray);

    } else if (id == 2) {
      a = 12;
      b = 'week';
      for (let i = 0; i < a; i++) {
        const currentDate = addWeeks(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
      console.log('Mảng tháng:', this.dateArray);

    } else if (id == 3) {
      let a = month; // Lấy 6 giá trị
      let b = 'month'; // Đơn vị thời gian là tháng

      // Chuyển đổi ngày đầu vào từ chuỗi sang kiểu Date
      inputDate = new Date('12-04-2023');

      for (let i = 0; i < a; i++) {
        // Thêm số tháng vào ngày đầu vào
        const currentDate = addMonths(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
      console.log('Mảng tháng:', this.dateArray);
    }
  }

  convertDateToVietnameseFormat(dateStr) {
    const dateParts = dateStr.split('/');
    const day = parseInt(dateParts[0], 10);
    const month = parseInt(dateParts[1], 10);
    const year = parseInt(dateParts[2], 10);

    const monthNames = [
      "tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6",
      "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"
    ];

    const result = `Ngày ${day} ${monthNames[month - 1]} năm ${year}`;

    return result;
  }

  pickDate() {
    this.showDatePicker = !this.showDatePicker;
  }

  hideDatePicker() {
    if (this.selectedDate) {
      const jsDate = new Date(this.selectedDate.year, this.selectedDate.month - 1, this.selectedDate.day);
      const formattedDate = format(jsDate, 'MM/dd/yyyy');
      this.showDatePicker = false; // Ẩn datepicker  }
      this.datePicker = formattedDate;
      this.datePickerShow = this.convertDateToVietnameseFormat(formattedDate);
      this.getListDay(this.pickServiceType, 6, this.datePicker);
      let dateDay = ''
      if(this.pickServiceType == 2){
        dateDay = this.getDateMonth(this.datePicker);
        this.scheduleDescription = dateDay + ' hàng tuần'
      } else if(this.pickServiceType == 3){
        dateDay = this.getDay(this.datePicker)
        this.scheduleDescription = 'Ngày' + dateDay + 'thàng tháng'
      }
    }
  }
}