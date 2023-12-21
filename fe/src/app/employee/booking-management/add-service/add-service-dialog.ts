import { Subscription } from 'rxjs';
import { ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';
import { AuthService } from 'src/app/services/auth.service';
import { Toast, ToastrService } from 'ngx-toastr';
import { addOnServiceData } from '../booking-management.component';

// import { DialogService } from 'src/app/services/dialog.service';

@Component({
  selector: 'app-add-service-dialog',
  templateUrl: './add-service-dialog.html',
  styleUrls: ['./add-service-dialog.css']
})

export class AddServiceManagementDialog implements OnDestroy, OnInit {
  @ViewChild("account", { static: false }) accountElement: ElementRef;

  public mobile: any;
  private _subscription = Subscription.EMPTY;
  name = '';
  body: any;
  price = 0;
  note = '';
  title_addon_service: any;
  idParent: any;


  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    private bookingServicee: BookingService,
    // private dialogService: DialogService,
    public serviceDialogRef: MatDialogRef<AddServiceManagementDialog>,
    private authService: AuthService,
    private toastr: ToastrService,
    @Inject(MAT_DIALOG_DATA) public data: addOnServiceData) {
  }

  ngOnInit(): void {
    this.title_addon_service = this.data.data;
    this.idParent = this.data.id;
  }


  onNoClick(): void {
    this.serviceDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  cleanerRateDetail() {

  }

  addService() {
    let body = {
      name: this.name,
      parentId: null,
      price: this.price,
      status: "ACTIVE"
    }

    if (this.idParent) {
      body = {
        name: this.name,
        parentId: this.idParent,
        price: this.price,
        status: "ACTIVE"
      }
    }

    if (body) {
      this.bookingServicee.addService(body).subscribe({
        next: () => {
          this.toastr.success('Nhóm dịch vụ tiện ích ' + this.name + ' đã được bổ sung');
          this.serviceDialogRef.close();
        },
        error: () => {
          this.toastr.error('Thêm dịch vụ tiện ích thất bại');
          // this.failureAddCleaner = true;
        },
      });
    }

  }


}