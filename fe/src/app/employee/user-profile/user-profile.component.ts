import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AuthService } from "src/app/services/auth.service";
import { CleanerService } from "src/app/services/cleaner.service";
// import { ApiService } from 'src/app/services/api.service';
// import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: "app-user-profile",
  templateUrl: "./user-profile.component.html",
  styleUrls: ["./user-profile.component.scss"],
})
export class UserProfileComponent implements OnInit {
  cleanerId: any;
  averageRating: any;
  name: any;
  dob: any;
  gender: any;
  address: any;
  status: any;
  activityYear: any;
  ratingNumber: any;
  data: any;
  id: any;
  branch: any;
  idCard: any;
  phoneNumber: any;
  email: any;
  history: any;
  review: any;
  jwtToken: any;
  cleaner: any;

  // private authService: AuthService, private apiService: ApiService
  constructor(
    private authService: AuthService,
    private cleanService: CleanerService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.jwtToken = this.authService.getJwtToken();
    let id = this.route.snapshot.paramMap.get('id');
    this.cleanService.getEmployeeById(id).subscribe( data => {
      this.cleaner = data;
      this.cleanerId = data.id;
    });
    this.cleanService.getCleanerHistoryDetail(id).subscribe( data => {
      this.data = data;
    });
    // this.data = {
    //   ratingOverview: {
    //     cleanerId: 1,
    //     name: "Nguyen Hoang Anh",
    //     idCard: "0123456789",
    //     email: "abc@gmail.com",
    //     phoneNumber: 84966069299,
    //     status: "active",
    //     branch: 1,
    //     activityYear: 0,
    //     averageRating: 5,
    //     ratingNumber: 2,
    //   },
    //   history: [
    //     {
    //       name: "Booking guest name",
    //       ratingScore: 5,
    //       workDate: "13/11/2023",
    //       houseType: "APARTMENT",
    //       floorNumber: 12,
    //       floorArea: 120.0,
    //       review:
    //         "Làm việc tích cực, nhanh gọn và sạch sẽ. Thái độ chuyên nghiệp và tỉ mỉ. Rất hài lòng.",
    //     },
    //     {
    //       name: "Booking guest name",
    //       ratingScore: 5,
    //       workDate: "13/11/2023",
    //       houseType: "APARTMENT",
    //       floorNumber: 12,
    //       floorArea: 120.0,
    //       review: "good",
    //     },
    //   ],
    // };
    //   [
    //   {
    //     "cleanerId": 1,
    //     "name": "Hoang anh",
    //     "idCard": "1",
    //     "email": null,
    //     "phoneNumber": null,
    //     "status": "ACTIVE",
    //     "branch": {
    //       "id": 1,
    //       "branchName": "Ha Noi",
    //       "branchAddress": null,
    //       "user": null,
    //       "description": null,
    //       "status": "ACTIVE",
    //       "createDate": "2023-11-14T13:15:29.945+00:00",
    //       "updateDate": "2023-11-14T13:15:29.945+00:00"
    //     },
    //     "activityYear": 0,
    //     "averageRating": 5,
    //     "ratingNumber": 2
    //   },
    //   {
    //     "cleanerId": 2,
    //     "name": "Kyo Duy",
    //     "idCard": "2",
    //     "email": null,
    //     "phoneNumber": null,
    //     "status": "ACTIVE",
    //     "branch": {
    //       "id": 1,
    //       "branchName": "Ha Noi",
    //       "branchAddress": null,
    //       "user": null,
    //       "description": null,
    //       "status": "ACTIVE",
    //       "createDate": "2023-11-14T13:15:29.945+00:00",
    //       "updateDate": "2023-11-14T13:15:29.945+00:00"
    //     },
    //     "activityYear": 0,
    //     "averageRating": 5,
    //     "ratingNumber": 2
    //   }
    // ]
    this.averageRating = this.data.ratingOverview.averageRating;
    this.name = this.data.ratingOverview.name;
    this.email = this.data.ratingOverview.email;
    this.phoneNumber = this.data.ratingOverview.phoneNumber;
    this.address = this.data.ratingOverview.address
      ? this.data.ratingOverview.address
      : null;
    this.idCard = this.data.ratingOverview.idCard;
    // this.dob = this.data.ratingOverview.dob;
    this.status = this.data.ratingOverview.status;
    this.branch = this.data.ratingOverview.branch.branchName;
    this.activityYear = this.data.ratingOverview.activityYear;
    this.ratingNumber = this.data.ratingOverview.ratingNumber;
    this.review = this.data.history[0].review;
    this.history = this.data.history;
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

    return stars.join("");
  }

  showMore(id: any) {}
}