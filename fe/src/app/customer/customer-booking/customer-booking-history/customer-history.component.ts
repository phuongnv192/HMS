import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { BookingService } from 'src/app/services/booking.service';

@Component({
  selector: 'app-customer-history',
  templateUrl: './customer-history.component.html',
  styleUrls: ['./customer-history.component.scss']
})
export class CustomerHistoryComponent implements OnInit {

  name = '';
  data: any;
  date: any;
  history: any;
  rateRange: string[];
  houseType: string[];
  searchRate: any;
  searchHouseType: string;
  customerId: any;
  constructor(private authService: AuthService,
    private bookingService: BookingService) { }

  ngOnInit() {
    this.authService.getUserInfor().subscribe(cus => {
      this.customerId = cus.data.id;
      this.bookingService.getBookingHistory(this.customerId).subscribe(res => {
        
      })
    })
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
          "review": "Làm việc tích cực , nhanh gọn và sạch sẽ. Thái độ chuyên nghiệp và tỉ mỉ. Rất hài lòng."
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
    this.history = this.data.history;
    this.searchRate = '4 - 5';
    this.searchHouseType = 'APARTMENT';
    this.houseType = ['APARTMENT', 'NORMAL', 'Villa'];
    this.rateRange = ['0 - 1', '1 - 2', '2 - 3', '3 - 4', '4 - 5'];
    this.date = '19/11/2023'

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

  search() {

  }

}