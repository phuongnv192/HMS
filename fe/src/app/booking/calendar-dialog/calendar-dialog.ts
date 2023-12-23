import { Subscription } from 'rxjs';
import { CalendarDialogData } from '../booking.component';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { addDays, addWeeks, format, addMonths, isBefore, isAfter, subMonths } from 'date-fns';
import { NgbDate, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { AddServiceDialog } from './add-service-dialog/add-service-dialog';

export interface AddServiceDialogData {
  data: any;
  addonService: any;
  servicePick: any;
}

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
  selectedServicePackageId = ''; // Biến lưu trữ loại dịch vụ sàn được chọn
  servicePackages: any[] = [];
  service: any;
  packagesTemp: any[] = [];
  pickServiceType: any;
  dateArray: any[] = [];
  selectedTimeType = true;
  selectedDate: NgbDateStruct | null = null;
  showDatePicker: boolean = true;
  datePicker: string;
  datePickerShow: any;
  scheduleDescription = '';
  selectedPick = {};
  servicePick: any;
  c_time = false;
  _inTime: any;
  flag: any;
  scheduleTimeDescription: any;
  dataPick = [];
  dateValue: string;
  typeId: any;
  dataPickTemp: any[];
  calendarResult: any;
  workDate: any;
  minSelectableDate: NgbDate;
  maxSelectableDate: NgbDate;

  constructor(
    public dialogRef: MatDialogRef<CalendarDialog>,
    public dialogRefAddOn: MatDialogRef<AddServiceDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CalendarDialogData,
    public dialog: MatDialog, private renderer: Renderer2,
    private ngZone: NgZone) {
      const today = new Date();
      this.minSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 1, today.getDate());
      this.maxSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 2, today.getDate());
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
    this.calendarResult = this.data.calendarResult;
    if (this.calendarResult) {
      console.log("this.calendarResult", this.calendarResult);
      this.selectedServiceTypeId = this.calendarResult.serviceTypeId;
      this.selectedServicePackageId = this.calendarResult.servicePackageId;
      this.pickDay = this.calendarResult.dateValue;      
      this.selectedDate = this.convertStringToNgbDate(this.pickDay);
      this.loadData();
      this.hideDatePicker();
      this.dataPick = this.calendarResult.schedule;
    } else {
      this.selectedServiceTypeId = this.data.type[0].serviceTypeName;
      this.selectedServicePackageId = this.data.type[0].servicePackages[0].servicePackageName + ' - ' + this.data.type[0].servicePackages[0].unit;
      this.pickDay = '';
      this.dataPick = [];
      this.loadData();
    }
    this.data.type.forEach(type => {
      if (this.selectedServiceTypeId == type.serviceTypeName) {
        this.typeId = type.serviceTypeId;
      }
    });
  }

  loadData(){
    this.data.type.forEach(type => {
      if (this.selectedServiceTypeId == type.serviceTypeName) {
        this.typeId = type.serviceTypeId;
      }
    });

    this.pickServiceType = this.getId(this.data.type, this.selectedServiceTypeId);    

    this.data.type.forEach(element => {
      this.serviceTypes.push(element.serviceTypeName);
    });

    this.packagesTemp = this.data.type[this.pickServiceType - 1].servicePackages;

    this.packagesTemp.forEach(element => {
      this.servicePackages.push(element.servicePackageName + ' - ' + element.unit);
    });
  }

  private convertStringToNgbDate(dateString: string): NgbDate | null {
      const dateParts = dateString.split('-');  
      const year = parseInt(dateParts[0], 10);
      const month = parseInt(dateParts[1], 10);
      const day = parseInt(dateParts[2], 10);
      console.log("new NgbDate(year, month, day)", year, month, day);
      
      return new NgbDate(year, month, day);
  }

  selectedService(event: any) {
    this.servicePackages = [];
    this.dataPick = [];
    // this.dateArray = [];
    // this.selectedDate = null;
    // this.showDatePicker = true;
    this.datePickerShow = false;

    this.pickServiceType = this.getId(this.data.type, this.selectedServiceTypeId);
    this.packagesTemp = this.data.type[this.pickServiceType - 1].servicePackages;
    this.selectedServicePackageId = this.data.type[this.pickServiceType - 1].servicePackages[0].servicePackageName + ' - ' + this.data.type[this.pickServiceType - 1].servicePackages[0].unit;

    this.packagesTemp.forEach(element => {
      this.servicePackages.push(element.servicePackageName + ' - ' + element.unit);
    });

    if (this.pickServiceType == 1 || this.pickServiceType == 3) {
      this.selectedTimeType = true;
    }

    this.data.type.forEach(type => {
      if (this.selectedServiceTypeId == type.serviceTypeName) {
        this.typeId = type.serviceTypeId;
      }
    })
    this.hideDatePicker();

  }

  selectedPackage(event: any) {
    this.dataPick = [];
    // this.servicePackages = [];
    // // this.dateArray = [];
    // // this.selectedDate = null;
    // // this.showDatePicker = true;
    // this.datePickerShow = false;

    // this.pickServiceType = this.getId(this.data.type, this.selectedServiceTypeId);
    // this.packagesTemp = this.data.type[this.pickServiceType - 1].servicePackages;

    // this.packagesTemp.forEach(element => {
    //   this.servicePackages.push(
    //     {
    //       title: element.servicePackageName + ' - ' + element.unit,
    //       value:  element.servicePackageId
    //     }
    // );
    // });

    // if (this.pickServiceType == 1 || this.pickServiceType == 3) {
    //   this.selectedTimeType = true;
    // }

    // this.data.type.forEach(type => {
    //   if(this.selectedServiceTypeId == type.serviceTypeName){
    //     this.typeId = type.serviceTypeId;
    //   }
    // })
    // this.hideDatePicker();

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

  pickCalendar() {
    this.dataPickTemp =
      [{
        servicePackageId: this.selectedServicePackageId,
        typeId: this.typeId,
        serviceTypeId: this.selectedServiceTypeId,
        schedule: this.dataPick,
        dateValue: this.dateValue,
        datePickerShow: this.datePickerShow + this.scheduleDescription
      }];

    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close(this.dataPickTemp);
      });
    }
  }

  getDay(todayin) {
    let today = new Date(todayin);
    let day = today.getDate().toString().padStart(2, '0');
    return day;
  }

  getMonth(todayin) {
    const inputDate = new Date(todayin); // Ngày tháng năm (MM-dd-yyyy)
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
      Sunday: "Chủ Nhật",
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
    const monthValue = today.getMonth() + 1; // Thêm 1 để chuyển từ 0-11 thành 1-12

    // Định dạng giá trị tháng thành chuỗi
    let formattedMonth = monthValue.toString();
    return today.getDate() + '/' + formattedMonth + '/' + today.getFullYear();
  }


  getListDay(id: any, month: any, date: string) {
    console.log("id", id, month, date);
    
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
    } else if (id == 2) {
      a = 12;
      b = 'week';
      for (let i = 0; i < a; i++) {
        const currentDate = addWeeks(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
    } else if (id == 3) {
      let a = month; // Lấy 6 giá trị
      let b = 'month'; // Đơn vị thời gian là tháng

      // Chuyển đổi ngày đầu vào từ chuỗi sang kiểu Date
      // inputDate = new Date(date);

      for (let i = 0; i < a; i++) {
        // Thêm số tháng vào ngày đầu vào
        const currentDate = addMonths(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
    }
  }

  convertDateToVietnameseFormat(dateStr) {
    const dateParts = dateStr.split('/');
    const day = parseInt(dateParts[1], 10);
    const month = parseInt(dateParts[0], 10);
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

  datePickerNavigate(event: any) {
    // Chặn ngày trước hôm nay và sau hôm nay 1 tháng
    const today = new Date();
    const minDate = subMonths(today, 1);
    const maxDate = new Date(today);
    maxDate.setHours(23, 59, 59); // Đặt thời gian cuối ngày để bao gồm toàn bộ ngày hôm nay

    if (isBefore(event.current, minDate) || isAfter(event.current, maxDate)) {
      // Nếu ngày không nằm trong khoảng cho phép, chuyển về tháng hiện tại
      event.current = new NgbDate(today.getFullYear(), today.getMonth(), today.getDate());
    }
  }

  hideDatePicker() {
    if (this.selectedDate) {
      console.log("selectedDate", this.selectedDate);
      const jsDate = new Date(this.selectedDate.year, this.selectedDate.month - 1, this.selectedDate.day);
      const formattedDate = format(jsDate, 'MM/dd/yyyy');
      this.showDatePicker = false; // Ẩn datepicker  }
      this.datePicker = formattedDate;
      const formattedDate2 = format(jsDate, 'yyyy-MM-dd');
      this.dateValue = formattedDate2;
      this.datePickerShow = this.convertDateToVietnameseFormat(formattedDate);
      this.getListDay(this.pickServiceType, 6, this.datePicker);
      let dateDay = ''
      if (this.pickServiceType == 2) {
        dateDay = this.getDateMonth(this.datePicker);
        this.scheduleDescription = ' - ' + dateDay + ' hàng tuần'
      } else if (this.pickServiceType == 3) {
        dateDay = this.getDay(this.datePicker)
        this.scheduleDescription = ' - Ngày ' + dateDay + ' hàng tháng'
      } else if(this.pickServiceType == 1){
        this.scheduleDescription = ' - Lịch dọn hàng ngày'
      }
    }
  }

  viewDetailinSchedule(showtime: any, index: any) {
    // this.selectedDate = showtime;
    const existingIndex = this.dataPick.findIndex(item => item.index === index);    
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRefAddOn = this.dialog.open(AddServiceDialog, {
      width: '500px',
      maxHeight: '50%',
      data: {
        data: showtime,
        addonService: this.data.addonService,
        servicePick: this.dataPick[existingIndex] ? this.dataPick[existingIndex] : []
      },
      panelClass: ['add-service']
    });

    this.dialogRefAddOn.afterClosed().subscribe(result => {
      if (result) {
        const existingIndex = this.dataPick.findIndex(item => item.index === index);

        if (existingIndex !== -1) {
          // Nếu đã tồn tại, thì cập nhật giá trị của mảng đó
          if(result.length < 1){
            this.dataPick.splice(existingIndex, 1);
          } else {
            this.dataPick[existingIndex] = {
              floorNumber: 1,
              workDate: showtime,
              startTime: null,
              endTime: null,
              serviceAddOnIds: result,
              index: index  // Giữ nguyên index để xác định mảng cần cập nhật
            };
          }
          
        } else {
          // Nếu chưa tồn tại, thì thêm mảng mới vào this.dataPick
          this.dataPick.push({
            floorNumber: 1,
            workDate: showtime,
            startTime: null,
            endTime: null,
            serviceAddOnIds: result,
            index: index  // Gán index để xác định mảng
          });
        }
        if (this.selectedPick) {
          this.selectedPick[index] = [...result];
        }
      }
      // console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

  ValidateExpDate(_val: any, event) {
    this.c_time = false;
    // this.error_post_message = '';
    var ua = navigator.userAgent.toLowerCase();
    let v = '';
    // check for safari macbook
    if (ua.indexOf('safari') != -1 && this.isMacintosh()) {
      if (ua.indexOf('chrome') > -1) {
        this._inTime = _val.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
        v = this._inTime;
      } else {
        if (_val.value) {
          if (event.keyCode === 8 || event.key === "Backspace" || event.inputType == 'deleteContentBackward') {
            if (event.target.name == 'exp_date' && this._inTime) {
              if (this._inTime.length != 3) {
                this._inTime = this._inTime.slice(0, -1);
              } else {
                this._inTime = this._inTime.slice(0, -2);
              }
            }
            event.preventDefault();
          } else if (!isNaN(_val.value.substr(_val.value.length - 1)) && event.inputType == 'insertCompositionText') {
            this._inTime = this._inTime + _val.value.substr(_val.value.length - 1);
          }
        } else {
          this._inTime = '';
        }
        v = this._inTime;
      }
    } else {
      this._inTime = _val.value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
      v = this._inTime;
    }
    const newdate = this.time_format(v);
    _val.value = newdate;
    this._inTime = newdate;
    if (this.validTime(this._inTime)) {
      this.c_time = false;
    } else {

    }
    this.enablePayNow();
  }


  isMacintosh() {
    return navigator.platform.toLowerCase().indexOf('mac') > -1
  }

  time_format(value) {
    let v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    if (v.length === 2 && this.flag.length === 3 && this.flag.charAt(this.flag.length - 1) === ':') {
      v = '';
    }
    if (v && !v.startsWith('0') && !v.startsWith('1')) {
      if (v.length === 1) {
        v = '0' + v + ':';
      } else if (v.length === 2) {
        v = v + ':';
      }
    } else if ((v <= 19 && v >= 8) || v.length === 2) {
      v = v + ':';
    }
    const matches = v.match(/\d{2,4}/g);
    const match = matches && matches[0] || '';
    const parts = [];
    for (let i = 0, len = match.length; i < len; i += 2) {
      parts.push(match.substring(i, i + 2));
      if (len === 2) {
        parts.push('');
      }
    }
    if (parts.length) {
      this.flag = parts.join(':');
      return parts.join(':');
    } else {
      this.flag = value;
      return value;
    }
  }

  public blurExpDate(_val: any): void {
    this.scheduleTimeDescription = '';
    this.c_time = !(this.validTime(this._inTime));
    const v = this._inTime.replace(/\s+/g, '').replace(/[^0-9]/gi, '');
    const newdate = this.time_format(v);
    _val.value = newdate;
    this._inTime = newdate;
    if (!this.c_time) {
      this.scheduleTimeDescription = this.convertTimeFormat(newdate);
    }
  }

  convertTimeFormat(inputTime: string): string {
    // Kiểm tra xem inputTime có đúng định dạng hh:mm không
    const timeRegex = /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/;
    if (!timeRegex.test(inputTime)) {
      // Nếu không đúng định dạng, trả về thông báo lỗi hoặc giá trị ban đầu
      return 'Invalid time format';
    }

    // Chuyển đổi thành đối tượng Date để dễ xử lý-
    const inputDate = new Date(`1970-01-01T${inputTime}:00Z`);

    // Lấy giờ và phút
    const hours = inputDate.getHours();
    const minutes = inputDate.getMinutes();

    // Tạo chuỗi kết quả
    const result = `${hours} giờ ${minutes} phút`;

    return result;
  }

  validTime(value) {
    return ((value.length == 4 && value.search('/') == -1) || value.length == 5) && parseInt(value.split(':')[0], 0) >= 8
      && (parseInt(value.split(':')[0], 0) < 20) && (parseInt(value.split(':')[1], 0) <= 60)
  }

  removeAddOns() {

  }

  enablePayNow() {

  }
}