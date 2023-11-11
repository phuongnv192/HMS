import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})

export class BookingComponent implements OnInit {
  focus: any;
  focus1: any;
  floors: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]; // Mảng từ 1 đến 10
  houseTypes: string[] = ['Nhà đất', 'Chung cư'];
  serviceTypes: string[] = ['Tổng vệ sinh', 'Theo khu vực/Diện tích'];
  areaTypes: string[] = ['dưới 30m2', 'từ 30 - 50m2', 'trên 50m2', 'từ 50-70m2', 'trên 70m2'];
  selectedFloors: number; // Biến lưu trữ số tầng được chọn
  selectedHouseType: string; // Biến lưu trữ loại hình nhà được chọn
  selectedAreaType: string; // Biến lưu trữ loại diện tích sàn ước chừng được chọn
  selectedServiceType: string; // Biến lưu trữ loại diện tích sàn ước chừng được chọn
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


  constructor() { }

  ngOnInit() {
    this.selectedFloors = this.floors[0];
    this.selectedHouseType = this.houseTypes[0];
    this.selectedAreaType = this.areaTypes[0];
    this.selectedServiceType = this.serviceTypes[0];

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
}