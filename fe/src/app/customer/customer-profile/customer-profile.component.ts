import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.scss']
})
export class CustomerProfileComponent implements OnInit {
  public gender ='';
  public address = '';
  public data: any;
  public id: any;
  public phoneNumber = '';
  public email = '';
  public totalBooking = '';
  public activityYear = '';
  public firstName = '';
  public lastName = '';


  constructor(
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.getUserInfor().subscribe(data => {
      this.data = data.data;
      this.id = this.data.id;
      this.firstName = this.data.firstName;
      this.gender = this.data.gender == 'Male' ? 'Nam' : 'Nữ';
      this.lastName = this.data.lastName;
      this.email = this.data.email;
      this.phoneNumber = this.data.phoneNumber;
      this.address = this.data.address ? this.data.address : null;
      this.activityYear = this.data.activityYear ? this.data.activityYear : 0;
      this.totalBooking = this.data.totalBooking ? this.data.totalBooking : 0;
    })
  }

  showMore(id: any) {

  }
}
