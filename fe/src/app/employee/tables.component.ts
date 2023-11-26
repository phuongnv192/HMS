import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class ListCleanerComponent implements OnInit {
  listCleaner: any;
  searchAge: any;
  searchRate: any;
  searchStatus: any;
  ageRange: any;
  rateRange: any;
  statusRange: any;
  empName = '';

  constructor() { }

  ngOnInit() {
    this.ageRange = ['18 - 25', '25 - 35', '35 - 45', '> 45'];
    this.rateRange = ['0 - 1', '1 - 2', '2 - 3', '3 - 4', '4 - 5'];
    this.statusRange = ['Đang hoạt động', 'Không hoạt động'];
    this.listCleaner = [
      {
        cleanerId: 1,
        name: "Nhân viên 1",
        dob: "23/06/1999",
        gender: "Nam",
        address: "Số 1 Kim mã, Ba Đình, Hà Nội",
        status: "active",
        activityYear: 0,
        averageRating: 5,
        ratingNumber: 16
      },
      {
        cleanerId: 2,
        name: "Nhân viên 2",
        dob: "11/07/1998",
        gender: "Nam",
        address: "Số 2 Kim mã, Ba Đình, Hà Nội",
        status: "inactive",
        activityYear: 0,
        averageRating: 1.5,
        ratingNumber: 9
      },
      {
        cleanerId: 3,
        name: "Nhân viên 3",
        dob: "01/08/2003",
        gender: "Nam",
        address: "Số 3 Kim mã, Ba Đình, Hà Nội",
        status: "inactive",
        activityYear: 0,
        averageRating: 4.5,
        ratingNumber: 13
      },
      {
        cleanerId: 4,
        name: "Nhân viên 4",
        dob: "22/09/1994",
        gender: "Nam",
        address: "Số 4 Kim mã, Ba Đình, Hà Nội",
        status: "active",
        activityYear: 0,
        averageRating: 3.5,
        ratingNumber: 22
      },
      {
        cleanerId: 5,
        name: "Nhân viên 5",
        dob: "23/10/2000",
        gender: "Nam",
        address: "Số 1 Kim mã, Ba Đình, Hà Nội",
        status: "active",
        activityYear: 0,
        averageRating: 2,
        ratingNumber: 42
      },
    ]

  }

  removeUser() {

  }

  changeStatus() {

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

    return stars.join('');
  }

  searchEmp() {

  }

}