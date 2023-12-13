import { Component, ElementRef, Inject, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NgbDateStruct, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { format, isAfter, isBefore, subMonths } from 'date-fns';
// import { DialogService } from 'src/app/services/dialog.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CalendarDialog } from './calendar-dialog/calendar-dialog';
import { PickCleanerDialog } from './pick-cleaner-dialog/pick-cleaner-dialog';
import { BookingService } from '../services/booking.service';
import { PriceListDialog } from './price-list-dialog/price-list-dialog';

export interface DialogData {
  data: string;
}

export interface CleanerData {
  data: string;
}

export interface PickCleanerData {
  data: any;
  date: any;
}

export interface CalendarDialogData {
  type: any;
  pickDay: any;
  addonService: any;
}

export interface PriceListDialogData {
  data: any;
}

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})

export class BookingComponent implements OnInit {

  focus: any;
  focus1: any;
  note: any;

  showDatePicker = false;
  selectedDate: NgbDateStruct;
  listAdvanceService = [];
  activeBadges: { [key: string]: boolean } = {};
  floors: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]; // Mảng từ 1 đến 10
  houseTypes: string[] = ['Nhà đất', 'Chung cư'];
  serviceTypes: string[] = ['Tổng vệ sinh', 'Theo khu vực/Diện tích'];
  timeTypes: string[] = ['Sử dụng 1 lần', 'Định kỳ'];
  areaTypes: any[] = [
    {
      title: 'dưới 30m2',
      value: 30
    },
    {
      title: 'từ 30-50m2',
      value: 50
    },
    {
      title: 'từ 50-70m2',
      value: 70
    },
    {
      title: 'từ 70m2 - 90m2',
      value: 90
    },
    {
      title: 'từ 90m2-100m2',
      value: 100
    },
    {
      title: 'từ 100m2-120m2',
      value: 120
    },
    {
      title: 'từ 120m2-150m2',
      value: 150
    },
  ];
  selectedFloors: number; // Biến lưu trữ số tầng được chọn
  selectedHouseType: string; // Biến lưu trữ loại hình nhà được chọn
  selectedAreaType: string; // Biến lưu trữ loại diện tích sàn ước chừng được chọn
  selectedServiceType: string; // Biến lưu trữ loại dịch vụ sàn được chọn
  selectedTimeType: string; // Biến lưu trữ loại thời gian sàn  được chọn
  serviceExtend = [
    {
      name: 'Vệ sinh máy giặt',
      value: [
        { description: 'không tháo lồng', price: 100 },
        { description: 'có lồng', price: 150 }
      ]
    },
    {
      name: 'Vệ sinh tủ lạnh',
      value: [
        { description: 'Dung tích 90-160 lít', price: 50 },
        { description: 'Dung tích 180-250 lít', price: 70 },
        { description: 'Dung tích 400 - 600 lít', price: 90 },
        { description: 'Dung tích trên 600 lít', price: 120 }
      ]
    },
    {
      name: 'Vệ sinh đồ nấu nướng',
      value: [
        { description: 'Toàn bộ', price: 200 },
        { description: 'Lò nướng, lò vi sóng', price: 80 },
        { description: 'Bếp gas', price: 60 }
      ]
    },
    {
      name: 'Vệ sinh điều hòa',
      value: [
        { description: 'Điều hòa thường', price: 200 },
        { description: 'Điều hòa công nghiệp', price: 80 },
        { description: 'Điều hòa cây', price: 60 }
      ]
    }
    // Thêm các phần tử khác nếu cần
  ];
  minSelectableDate: NgbDate;
  maxSelectableDate: NgbDate;
  datePicker: string;
  datePickerShow: any;
  type: any;
  listDay: any;
  selectedPaymentMethod: any;
  termAndCondition: boolean;
  private TermsDialogRef: any;
  private termCondition: any;
  totalAmount: any;
  validPayment = true;
  data: any;
  dataCleaner: any;
  pickDay: any;
  serviceTypeData: any;
  priceList: any;
  textListAdvanceService: string;
  public account_name = '';
  public phone_number = '';
  res: any;
  cleanerNum: any;
  duration: any;
  dateValue: string;
  public service_address = '';
  @ViewChild("termsDiv") termsDiv: ElementRef<HTMLElement>;
  flag: any;
  scheduleTimeDescription: string;
  c_time: boolean;
  public _inTime: any;


  onResize(event) {
    if (this.TermsDialogRef) {
      this.TermsDialogRef.close();
      let el: HTMLElement = this.termsDiv.nativeElement;
      el.click();
    }
  }

  constructor(
    private router: Router,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public dialogRef: MatDialogRef<CalendarDialog>,
    public dialogRefPriceList: MatDialogRef<PriceListDialog>,
    public cleanerDialogRef: MatDialogRef<PickCleanerDialog>,
    private bookingServicee: BookingService
  ) {
    const today = new Date();
    this.minSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 1, today.getDate());
    this.maxSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 2, today.getDate());

  }

  ngOnInit() {
    this.bookingServicee.getServiceType().subscribe(data => {
      if (data) this.serviceTypeData = data.data;
    });

    this.bookingServicee.getServiceAddOns("-1").subscribe(data => {
      if (data) this.priceList = data.data;
    });

    this.bookingServicee.getDataService().subscribe(res => {
      this.res = res.data;
    });
    this.selectedPaymentMethod = 'cash';
    this.selectedFloors = this.floors[0];
    this.selectedHouseType = this.houseTypes[0];
    this.selectedAreaType = this.areaTypes[0].title;
    this.selectedServiceType = this.serviceTypes[0];
    this.selectedTimeType = this.timeTypes[0];

  }

  pickDate() {
    this.showDatePicker = !this.showDatePicker;
  }

  hideDatePicker() {
    if (this.selectedDate) {
      const jsDate = new Date(this.selectedDate.year, this.selectedDate.month - 1, this.selectedDate.day);
      const formattedDate = format(jsDate, 'dd/MM/yyyy');
      const formattedDate2 = format(jsDate, 'yyyy-MM-dd');
      this.dateValue = formattedDate2;
      this.showDatePicker = false; // Ẩn datepicker  }
      this.datePicker = formattedDate;
      this.datePickerShow = this.convertDateToVietnameseFormat(formattedDate);
    }
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

  extendsDialog(service: string) {
    const index = this.listAdvanceService.indexOf(service);
    this.activeBadges[service] = !this.activeBadges[service];
    if (index === -1) {
      // Nếu giá trị không tồn tại, thêm vào mảng
      this.listAdvanceService.push(service);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceService.splice(index, 1);
    }
    this.textListAdvanceService = this.listAdvanceService.join(', ')

  }

  pickCalendar(): void {
    // this.dialogService.sendDataDialog(true);
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(CalendarDialog, {
      width: '1000px',
      maxHeight: '60%',
      data: {
        type: this.serviceTypeData,
        pickDay: this.pickDay,
        addonService: this.priceList
      },
      panelClass: ['calendar-pick', 'custom-margin']
    });

    this.dialogRef.afterClosed().subscribe(result => {
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

  FieldsChangeTermAndCondition(values: any) {
    this.termAndCondition = true;
  }

  openTermAndConditions() {
    this.TermsDialogRef = this.dialog.open(TermAndConditionDialog, {
      width: "800px",
      // height: '90%',
      maxHeight: "80vh",
      data: { data: "ahihi" },
    });
    this.TermsDialogRef.afterClosed().subscribe((result) => {
      this.TermsDialogRef = null;
    });
  }

  pickCleaner() {
    // this.dialogService.sendDataDialog(true);
    if (this.selectedTimeType == 'Sử dụng 1 lần') {
      this.bookingServicee.getCleanerAvaiable(this.dateValue, '', '').subscribe(item => {
        this.dataCleaner = item.data;
        this.renderer.addClass(document.body, 'modal-open');
        this.cleanerDialogRef = this.dialog.open(PickCleanerDialog, {
          width: '1000px',
          maxHeight: '85%',
          data: {
            data: this.dataCleaner,
            date: this.pickDay,

          },
          panelClass: ['pick-cleaner']
        });
        this.cleanerDialogRef.afterClosed().subscribe(result => {
          this.renderer.removeClass(document.body, 'modal-open');
          // this.dialogService.sendDataDialog(false);
        });
      })
    } else {
      this.bookingServicee.getCleanerAvaiable('2023-11-24', 3, 3).subscribe(item => {
        this.dataCleaner = item.data;
        this.renderer.addClass(document.body, 'modal-open');
        this.cleanerDialogRef = this.dialog.open(PickCleanerDialog, {
          width: '1000px',
          maxHeight: '85%',
          data: {
            data: this.dataCleaner,
            date: this.pickDay,

          },
          panelClass: ['pick-cleaner']
        });
        this.cleanerDialogRef.afterClosed().subscribe(result => {
          this.renderer.removeClass(document.body, 'modal-open');
          // this.dialogService.sendDataDialog(false);
        });
      })
    }


  }

  Order() {

  }

  showPriceList(): void {
    // this.dialogService.sendDataDialog(true);
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRefPriceList = this.dialog.open(PriceListDialog, {
      width: '1200px',
      maxHeight: '80%',
      data: {
        data: this.priceList,
      },
      panelClass: ['price-list']
    });

    this.dialogRefPriceList.afterClosed().subscribe(result => {
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

  onAreaChange(value: any) {
    let found = false; // Biến kiểm soát vòng lặp
    this.res.forEach(element => {
      if (!found && value < element.floorArea) {
        // Nếu tìm thấy phần tử thỏa mãn điều kiện, lưu giá trị và thoát vòng lặp
        this.cleanerNum = element.cleanerNum;
        this.duration = element.duration;
        found = true;
      }
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
    // this.enablePayNow();
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

    // Chuyển đổi thành đối tượng Date để dễ xử lý
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
}

@Component({
  selector: "term-and-condition-dialog",
  templateUrl: "term-condition-dialog/term-and-condition.html",
  styleUrls: ["term-condition-dialog/term-and-condition.scss"],
})

export class TermAndConditionDialog {
  cancel_description = "";

  constructor(
    public DialogRef: MatDialogRef<TermAndConditionDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {
  }

  onNoClick(): void {
    this.DialogRef.close();
  }
}