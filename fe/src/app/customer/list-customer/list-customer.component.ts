import { Component, OnInit, Renderer2 } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";
import { CustomerService } from "src/app/services/customer.service";
import { AddCleanerDialog } from "../../employee/add-cleaner/add-cleaner-dialog";

export interface CleanerData {
  data: string;
}

@Component({
  selector: "app-list-customer",
  templateUrl: "./list-customer.component.html",
  styleUrls: ["./list-customer.component.scss"],
})


export class ListCustomerComponent implements OnInit {
  listCustomer: any;
  cusName = "";
  data: any;

  constructor(private customerService: CustomerService,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public dialogRef: MatDialogRef<AddCleanerDialog>,
    private toastr: ToastrService,
    ) {}

  ngOnInit() {    
    this.customerService.getCustomers(null).subscribe({
      next: (res) => {
        if (res && res.data) {
          this.listCustomer = res.data;
        } else {
          this.listCustomer = [];
        }
      },
      error: (error) => {
        console.log(error);
      },
    });

    // this.listCustomer = [
    //   {
    //     customerId: 1,
    //     name: "Nhân viên 1",
    //     dob: "23/06/1999",
    //     gender: "Nam",
    //     address: "Số 1 Kim mã, Ba Đình, Hà Nội",
    //     totalBooking: 10,
    //   },
    //   {
    //     customerId: 2,
    //     name: "Nhân viên 2",
    //     dob: "11/07/1998",
    //     gender: "Nam",
    //     address: "Số 2 Kim mã, Ba Đình, Hà Nội",
    //     totalBooking: 2,
    //   },
    //   {
    //     customerId: 3,
    //     name: "Nhân viên 3",
    //     dob: "01/08/2003",
    //     gender: "Nam",
    //     address: "Số 3 Kim mã, Ba Đình, Hà Nội",
    //     totalBooking: 16,
    //   },
    //   {
    //     customerId: 4,
    //     name: "Nhân viên 4",
    //     dob: "22/09/1994",
    //     gender: "Nam",
    //     address: "Số 4 Kim mã, Ba Đình, Hà Nội",
    //     totalBooking: 2,
    //   },
    //   {
    //     customerId: 5,
    //     name: "Nhân viên 5",
    //     dob: "23/10/2000",
    //     gender: "Nam",
    //     address: "Số 1 Kim mã, Ba Đình, Hà Nội",
    //     totalBooking: 5,
    //   },
    // ];
  }

  removeUser() {}

  changeStatus() {}

  searchCustomer() {}

  
}