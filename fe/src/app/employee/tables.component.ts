import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-tables',
  templateUrl: './tables.component.html',
  styleUrls: ['./tables.component.scss']
})
export class TablesComponent implements OnInit {
  listCleaner: any;

  constructor() { }

  ngOnInit() {
    this.listCleaner = [
      {
        cleanerId: 1,
        name: "Longtk",
        dob: "23/06/1999",
        gender: "Nam",
        address: "Số 1 Kim mã, Ba Đình, Hà Nội",
        status: "Đang làm việc",
        activityYear: 0,
        averageRating: 5,
        ratingNumber: 2
      }
    ]
  }

  removeUser(){

  }

  changeStatus(){

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

}
