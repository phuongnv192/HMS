import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/services/api.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  cleanerId: any;
  name: any
  dob: any
  gender: any;
  address: any
  data: any;
  id: any;
  branch: any;
  idCard: any;
  phoneNumber: any;
  email: any;
  history: any;
  jwtToken: any;
  totalBooking: any;
  activityYear: any;


  constructor(
    // private authService: AuthService, private apiService: ApiService
  ) { }

  ngOnInit() {
    // this.jwtToken = this.authService.getJwtToken();
    // console.log("this.jwtToken", this.jwtToken);
    // this.apiService.getUsers().subscribe( data => {
    //   this.data = data;
    // })
    this.data = {
      "ratingOverview": {
        "userId": 1,
        "name": "Nguyen Hanh Nguyen",
        "idCard": "0123456789",
        "email": "abc@gmail.com",
        "phoneNumber": 84966069299,
        "status": "active",
        "branch": 1,
        "activityYear": 0,
        "totalBooking": 1,
      },
      "history": [
        {
          "name": "Booking guest name",
          "ratingScore": 5,
          "workDate": "13/11/2023",
          "houseType": "APARTMENT",
          "floorNumber": 12,
          "floorArea": 120.0,
          "review": "Làm việc tích cực, nhanh gọn và sạch sẽ. Thái độ chuyên nghiệp và tỉ mỉ. Rất hài lòng."
        },
        {
          "name": "Booking guest name",
          "ratingScore": 5,
          "workDate": "13/11/2023",
          "houseType": "APARTMENT",
          "floorNumber": 12,
          "floorArea": 120.0,
          "review": "good"
        }
      ]
    }
    this.id = this.data.ratingOverview.userId;
    this.name = this.data.ratingOverview.name;
    this.email = this.data.ratingOverview.email;
    this.phoneNumber = this.data.ratingOverview.phoneNumber;
    this.address = this.data.ratingOverview.address ? this.data.ratingOverview.address : null;
    this.idCard = this.data.ratingOverview.idCard;
    // this.dob = this.data.ratingOverview.dob;
    // this.status = this.data.ratingOverview.status;
    this.activityYear = this.data.ratingOverview.activityYear;
    this.totalBooking = this.data.ratingOverview.totalBooking;
    this.history = this.data.history;

  }

  showMore(id: any) {

  }
}
