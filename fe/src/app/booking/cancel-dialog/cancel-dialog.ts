import { Subscription } from 'rxjs';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';

// import { DialogService } from 'src/app/services/dialog.service';
export interface CancelData {
  id: string;
  type: string;
}

@Component({
  selector: 'app-cancel-dialog',
  templateUrl: './cancel-dialog.html',
  styleUrls: ['./cancel-dialog.css']
})

export class CancelDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  id: string;
  type = 'customer';
  description = '';
  optionList = [];

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public cancelDialogRef: MatDialogRef<CancelDialog>,
    private bookingServicee: BookingService,
    @Inject(MAT_DIALOG_DATA) public data: CancelData) {
  }

  ngOnInit(): void {
    this.id = this.data.id;
    this.type = this.data.type;
    if(this.type == 'customer'){
      this.description = 'Người dùng/Khách hàng ';
      this.optionList = [
        { name: 'Tôi muốn thay đổi lịch', isChecked: false },
        { name: 'Hủy vì lý do thay đổi nơi dọn', isChecked: false },
        { name: 'Hủy vì muốn thay đổi loại dịch vụ', isChecked: false },
        { name: 'Hủy vì sai thông tin', isChecked: false },
        { name: 'Khác', isChecked: false },
      ];    
    } else if(this.type == 'cleaner'){
      this.description = 'Người dùng/Nhân viên ';
      this.optionList = [
        { name: 'Xác thực đơn không khớp thông tin dọn', isChecked: false },
        { name: 'Không liên hệ được với người đặt', isChecked: false },
      ];
    }
  }

  showTextArea: boolean = false;
  otherReason: string = '';

  checkService(option: any) {
      this.showTextArea = this.optionList.some(option => option.name === 'Khác' && option.isChecked);
  }


  onNoClick(): void {
    this.cancelDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  submitReview() {

  }

  confirm(id: any) {
    let body = {
      bookingId: id,
      note:this.submitForm()
    }
    this.bookingServicee.cancelBooking(body).subscribe({
      next: (res) => {
        this.cancelDialogRef.close();

        // this.dialogRef.close(true);
      }
    });
  }

  submitForm() {
    const checkedOptions = this.optionList
      .filter(option => option.isChecked)
      .map(option => option.name);

    let resultString = checkedOptions.join(', ');

    if (this.showTextArea) {
      // Append the value of the text area to the resultString
      resultString += `, ${this.otherReason}`;
    }

    console.log(resultString); // or do whatever you want with the resultString
  }

}
