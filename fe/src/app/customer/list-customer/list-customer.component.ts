import { Component, OnInit } from "@angular/core";
import { CustomerService } from "src/app/services/customer.service";

@Component({
  selector: "app-list-customer",
  templateUrl: "./list-customer.component.html",
  styleUrls: ["./list-customer.component.scss"],
})
export class ListCustomerComponent implements OnInit {
  listCustomer: any;
  cusName = "";

  constructor(private customerService: CustomerService) {}

  ngOnInit() {
    this.customerService.getCustomers(null).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: () => {},
    });

    this.listCustomer = [
      {
        customerId: 1,
        name: "Nhân viên 1",
        dob: "23/06/1999",
        gender: "Nam",
        address: "Số 1 Kim mã, Ba Đình, Hà Nội",
        totalBooking: 10,
      },
      {
        customerId: 2,
        name: "Nhân viên 2",
        dob: "11/07/1998",
        gender: "Nam",
        address: "Số 2 Kim mã, Ba Đình, Hà Nội",
        totalBooking: 2,
      },
      {
        customerId: 3,
        name: "Nhân viên 3",
        dob: "01/08/2003",
        gender: "Nam",
        address: "Số 3 Kim mã, Ba Đình, Hà Nội",
        totalBooking: 16,
      },
      {
        customerId: 4,
        name: "Nhân viên 4",
        dob: "22/09/1994",
        gender: "Nam",
        address: "Số 4 Kim mã, Ba Đình, Hà Nội",
        totalBooking: 2,
      },
      {
        customerId: 5,
        name: "Nhân viên 5",
        dob: "23/10/2000",
        gender: "Nam",
        address: "Số 1 Kim mã, Ba Đình, Hà Nội",
        totalBooking: 5,
      },
    ];
  }

  removeUser() {}

  changeStatus() {}

  searchCustomer() {}
}