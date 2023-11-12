import { Component, OnInit } from '@angular/core';
import { NgbDateStruct, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { format, isAfter, isBefore, subMonths } from 'date-fns';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})

export class BookingComponent implements OnInit {
  focus: any;
  focus1: any;

  showDatePicker = false;
  selectedDate: NgbDateStruct;

  floors: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]; // Mảng từ 1 đến 10
  houseTypes: string[] = ['Nhà đất', 'Chung cư'];
  serviceTypes: string[] = ['Tổng vệ sinh', 'Theo khu vực/Diện tích'];
  timeTypes: string[] = ['Sử dụng 1 lần', 'Định kỳ'];
  areaTypes: string[] = ['dưới 30m2', 'từ 30 - 50m2', 'trên 50m2', 'từ 50-70m2', 'trên 70m2'];
  selectedFloors: number; // Biến lưu trữ số tầng được chọn
  selectedHouseType: string; // Biến lưu trữ loại hình nhà được chọn
  selectedAreaType: string; // Biến lưu trữ loại diện tích sàn ước chừng được chọn
  selectedServiceType: string; // Biến lưu trữ loại dịch vụ sàn được chọn
  selectedTimeType: string; // Biến lưu trữ loại thời gian sàn  được chọn
  serviceExtend = [
    {
      name: 'Vệ sinh máy giặt',
      value: [
        { description: 'Vệ sinh máy giặt không tháo lồng', price: 100 },
        { description: 'Vệ sinh và bảo trì máy giặt lồng', price: 150 }
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


  constructor() {
    const today = new Date();
    this.minSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 1, today.getDate());
    this.maxSelectableDate = new NgbDate(today.getFullYear(), today.getMonth() + 2, today.getDate());
    console.log("maxSelectableDate", this.maxSelectableDate);

  }

  ngOnInit() {
    this.selectedFloors = this.floors[0];
    this.selectedHouseType = this.houseTypes[0];
    this.selectedAreaType = this.areaTypes[0];
    this.selectedServiceType = this.serviceTypes[0];
    this.selectedTimeType = this.timeTypes[0];

    //   {
    //     "user_name": "abc",
    //     "user_phone": 0966069299,
    //     "location": "abcxyz",
    //     "houseType": "Nhà đất",
    //     "serviceType": "Tổng vệ sinh",
    //     "floor": 1,
    //     "area": 250,
    //     "serviceExtend": [
    //         {
    //           name: 'Vệ sinh máy giặt',
    //           value: [
    //             { description: 'Vệ sinh máy giặt không tháo lồng', price: 100, quantity: 2 },
    //             { description: 'Vệ sinh và bảo trì máy giặt lồng', price: 150, quantity: 1 }
    //           ]
    //         },
    //         {
    //           name: 'Vệ sinh tủ lạnh',
    //           value: [
    //             { description: 'Dung tích 90-160 lít', price: 50, quantity: 3 },
    //             { description: 'Dung tích 180-250 lít', price: 70, quantity: 2 },
    //             { description: 'Dung tích 400 - 600 lít', price: 90, quantity: 1 },
    //             { description: 'Dung tích trên 600 lít', price: 120, quantity: 1 }
    //           ]
    //         },
    //         {
    //           name: 'Vệ sinh đồ nấu nướng',
    //           value: [
    //             { description: 'Toàn bộ', price: 200, quantity: 1 },
    //             { description: 'Lò nướng, lò vi sóng', price: 80, quantity: 2 },
    //             { description: 'Bếp gas', price: 60, quantity: 1 }
    //           ]
    //         }
    //         // Thêm các phần tử khác nếu cần
    //     ]
    //     "date": {
    //       "a": "Ngày A",
    //       "b": "Ngày B",
    //       "c": "Ngày C"
    //     },
    //     "cleaner": {   //hoặc chuỗi rỗng = random
    //       "a": "anh A",
    //       "b": "chị B",
    //     },
    //     "note":"abcabcabc"
    //   }
    // }
  }

  pickDate() {
    this.showDatePicker = !this.showDatePicker;
  }

  hideDatePicker() {
    if (this.selectedDate) {
      const jsDate = new Date(this.selectedDate.year, this.selectedDate.month - 1, this.selectedDate.day);
      const formattedDate = format(jsDate, 'dd/MM/yyyy');
      console.log(formattedDate); // In giá trị theo định dạng giờ
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
}