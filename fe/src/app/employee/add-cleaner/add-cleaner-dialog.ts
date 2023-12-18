import { Subscription } from 'rxjs';
import { ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CleanerData } from '../../customer/list-customer/list-customer.component';
import { BookingService } from 'src/app/services/booking.service';
import { AuthService } from 'src/app/services/auth.service';
import { Toast, ToastrService } from 'ngx-toastr';

// import { DialogService } from 'src/app/services/dialog.service';

@Component({
  selector: 'app-add-cleaner-dialog',
  templateUrl: './add-cleaner-dialog.html',
  styleUrls: ['./add-cleaner-dialog.css']
})

export class AddCleanerDialog implements OnDestroy, OnInit {
  @ViewChild("account", { static: false }) accountElement: ElementRef;

  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public listCleaner: any;
  account_name: any;
  dataCleaner: any;
  body: any;
  account_email: any;
  houseTypes: string[] = ['Hà Nội'];
  selectedHouseType: string; // Biến lưu trữ loại hình nhà được chọn
  address: any;
  account_address = '';
  idCard = '';
  dob = '';
  listAdvanceServiceId: any;
  parentService = [];
  failureAddCleaner: boolean;

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    private bookingServicee: BookingService,
    // private dialogService: DialogService,
    public cleanerDialogRef: MatDialogRef<AddCleanerDialog>,
    private authService: AuthService,
    private toastr: ToastrService,
    @Inject(MAT_DIALOG_DATA) public data: CleanerData) {
  }

  ngOnInit(): void {
    this.selectedHouseType = 'Hà Nội';
    this.bookingServicee.getServiceAddOns("-1").subscribe(data => {
      if (data) {
        data.data.forEach(val => {
          let checkedValue = false;
          this.parentService.push({
            name: val,
            checked: checkedValue
          });
        });
      }
    });
  }


  onNoClick(): void {
    this.cleanerDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  cleanerRateDetail() {

  }

  addCleaner() {
    const body = {
      address: this.account_address,
      idCard: this.idCard,
      dob: this.dob ? this.dob : "1988-01-01",
      userId: this.account_name,
      branchId: "1",
      serviceIds: this.listAdvanceServiceId
    }
    if(this.body){
      this.authService.addCleaner(body).subscribe({
        next: () => {
          this.toastr.success('Người dọn ' + this.account_name + ' đã được bổ sung vào danh sách nhân viên');
          this.cleanerDialogRef.close();
        },
        error: () => { 
          this.toastr.error('Thêm người dọn thất bại');
          this.failureAddCleaner = true;
        },
      });
    }
    
  }

  omit_special_char_email(event) {
    var k;
    k = event.charCode; //
    if (/^[ ]{1,50}$/.test(event.key)) {
      return false;
    }
    return;
  }

  changeHandler(name: string) {
    if (name == "account") {
      this.account_name = this.change_specific_alias_email(
        this.account_name
          ? this.account_name.toString().trim().replace(/  +/g, " ")
          : ""
      );
    }
    if (name == "email") {
      this.account_email = this.change_specific_alias_email(
        this.account_email
          ? this.account_email.toString().trim().replace(/  +/g, " ")
          : ""
      );
    }
  }

  change_specific_alias_email(alias) {
    var str = alias;
    str = str.replace(
      /Ă |Ă¡|áº¡|áº£|Ă£|Ă¢|áº§|áº¥|áº­|áº©|áº«|Äƒ|áº±|áº¯|áº·|áº³|áºµ/g,
      "a"
    );
    str = str.replace(
      /Ă€|Ă|áº |áº¢|Ăƒ|Ă‚|áº¦|áº¤|áº¬|áº¨|áºª|Ä‚|áº°|áº®|áº¶|áº²|áº´/g,
      "A"
    );
    str = str.replace(/Ă¨|Ă©|áº¹|áº»|áº½|Ăª|á»|áº¿|á»‡|á»ƒ|á»…/g, "a");
    str = str.replace(/Ăˆ|Ă‰|áº¸|áºº|áº¼|Ă|á»€|áº¾|á»†|á»‚|á»„/g, "A");
    str = str.replace(/Ă¬|Ă­|á»‹|á»‰|Ä©/g, "a");
    str = str.replace(/ĂŒ|Ă|á»|á»ˆ|Ä¨/g, "A");
    str = str.replace(
      /Ă²|Ă³|á»|á»|Ăµ|Ă´|á»“|á»‘|á»™|á»•|á»—|Æ¡|á»|á»›|á»£|á»Ÿ|á»¡/g,
      "a"
    );
    str = str.replace(
      /Ă’|Ă“|á»Œ|á»|Ă•|Ă”|á»’|á»|á»˜|á»”|á»–|Æ |á»œ|á»|á»¢|á»|á» /g,
      "A"
    );
    str = str.replace(/Ă¹|Ăº|á»¥|á»§|Å©|Æ°|á»«|á»©|á»±|á»­|á»¯/g, "a");
    str = str.replace(/Ă™|Ă|á»¤|á»¦|Å¨|Æ¯|á»ª|á»¨|á»°|á»¬|á»®/g, "A");
    str = str.replace(/á»³|Ă½|á»µ|á»·|á»¹/g, "a");
    str = str.replace(/á»²|Ă|á»´|á»¶|á»¸/g, "A");
    str = str.replace(/Ä‘/g, "a");
    str = str.replace(/Ä/g, "A");
    str = str.replace(
      /!|%|\Ë†|\Ëœ|\â‚¬|\Â£|\Â¥|\â€¢|\â‚«|\^|\*|\(|\)|\+|\=|\<|\>|\?|\/|,|\:|\;|\'|\"|\&|\#|\[|\]|~|\$|`{|}|\||\\/g,
      ""
    );
    str = str.replace(/¡|£|¢|Ä|ƒ|ÄƑ|ä/g, "");
    str = str.replace(/¨|©|ª|»/g, "");
    str = str.replace(/¬|‹|‰|Ä/g, "");
    str = str.replace(/²|³||´|“|‘|™|•|—|Æ|›|µ|æ/g, "");
    str = str.replace(/¹|º|¥|§|å|°|«|±|¯/g, "");
    str = str.replace(/½|·|‘|æ|€/g, "");
    str = str.replace(/Õ|Ò|õ|ò/g, "o");
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
    str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A");
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
    str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E");
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
    str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I");
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
    str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O");
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
    str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U");
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
    str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y");
    str = str.replace(/đ/g, "d");
    str = str.replace(/Đ/g, "D");
    str = str.trim();
    return str;
  }

  checkService(id: any) {
    const idex = this.listAdvanceServiceId.indexOf(id);

    if (idex == -1) {
      this.listAdvanceServiceId.push(id);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceServiceId.splice(idex, 1);
    }
  }
}