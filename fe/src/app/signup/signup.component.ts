import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {
  @ViewChild('telephone', { static: false }) telephone: ElementRef;
  @ViewChild('password', { static: false }) password: ElementRef;
  @ViewChild('repassword', { static: false }) re_password: ElementRef;
  @ViewChild('email', { static: false }) emailElement: ElementRef;
//   @ViewChild('email') emailElement: any;
  @ViewChild('account') accountElement: any;

  isChecked: boolean = false;
  isRegistered: boolean = false;
  errorMessage = '';
  tel: string;
  pwd: string;
  repwd: string;
  reinput: any;
  test: Date = new Date();
  focus;
  focus1;
  focus2;
  focus4;
  focus3;
  account_name: any;
  account_email: any;
  accountBlur: boolean;
  emailBlur: boolean;
  emailExist = false;
  constructor(
    // private authService: AuthService
    // , private router: Router
  ) { }

  ngOnInit() { }

  verifyPassword(): boolean {
    const password = this.pwd;
    const repassword = this.repwd;
    return password === repassword;
  }

  signUp() {
    // const tel = this.tel;
    // // const account_name = this.account_name;
    // // const email = this.account_email;
    // const password = this.pwd;
    // console.log(this.verifyPassword());
    // if (!this.verifyPassword()) {
    //   this.reinput = true;
    // }
    // this.authService.register(tel, password).subscribe(
    //   response => {
    //     // Đăng ký thành công
    //     this.isRegistered = true;
    //     // Chuyển hướng sang trang Home và truyền thông báo thành công
    //     this.router.navigate(['/home'], { queryParams: { success: true } });
    //   },
    //   error => {
    //     // Đăng ký thất bại
    //     this.errorMessage = error.message;
    //     // Hiển thị thông báo lỗi
    //   }
    // );
  }

  omit_special_char_email(event) {
    var k;
    k = event.charCode;  //       
    if (/^[ ]{1,50}$/.test(event.key)) {
      return false;
    } return;
  }

  changeHandler(name: string) {
    if (name == 'account') {
      this.account_name = this.change_specific_alias_email(this.account_name ? this.account_name.toString().trim().replace(/  +/g, ' ') : '');
    }
    if (name == 'email') {
      this.account_email = this.change_specific_alias_email(this.account_email ? this.account_email.toString().trim().replace(/  +/g, ' ') : '');
    }

  }

  clearAccount() {
    this.accountBlur = false;
    this.account_name = '';
    this.accountElement.nativeElement.focus();
  }

  focusAccount() {
    this.accountBlur = false;
  }

  clearEmail() {
    this.emailBlur = false;
    this.account_email = '';
    this.emailElement.nativeElement.focus();
  }

  focusEmail() {
    this.emailBlur = false;
  }

  change_specific_alias_email(alias) {
    var str = alias;
    str = str.replace(/Ă |Ă¡|áº¡|áº£|Ă£|Ă¢|áº§|áº¥|áº­|áº©|áº«|Äƒ|áº±|áº¯|áº·|áº³|áºµ/g, 'a');
    str = str.replace(/Ă€|Ă|áº |áº¢|Ăƒ|Ă‚|áº¦|áº¤|áº¬|áº¨|áºª|Ä‚|áº°|áº®|áº¶|áº²|áº´/g, 'A');
    str = str.replace(/Ă¨|Ă©|áº¹|áº»|áº½|Ăª|á»|áº¿|á»‡|á»ƒ|á»…/g, 'a');
    str = str.replace(/Ăˆ|Ă‰|áº¸|áºº|áº¼|Ă|á»€|áº¾|á»†|á»‚|á»„/g, 'A');
    str = str.replace(/Ă¬|Ă­|á»‹|á»‰|Ä©/g, 'a');
    str = str.replace(/ĂŒ|Ă|á»|á»ˆ|Ä¨/g, 'A');
    str = str.replace(/Ă²|Ă³|á»|á»|Ăµ|Ă´|á»“|á»‘|á»™|á»•|á»—|Æ¡|á»|á»›|á»£|á»Ÿ|á»¡/g, 'a');
    str = str.replace(/Ă’|Ă“|á»Œ|á»|Ă•|Ă”|á»’|á»|á»˜|á»”|á»–|Æ |á»œ|á»|á»¢|á»|á» /g, 'A');
    str = str.replace(/Ă¹|Ăº|á»¥|á»§|Å©|Æ°|á»«|á»©|á»±|á»­|á»¯/g, 'a');
    str = str.replace(/Ă™|Ă|á»¤|á»¦|Å¨|Æ¯|á»ª|á»¨|á»°|á»¬|á»®/g, 'A');
    str = str.replace(/á»³|Ă½|á»µ|á»·|á»¹/g, 'a');
    str = str.replace(/á»²|Ă|á»´|á»¶|á»¸/g, 'A');
    str = str.replace(/Ä‘/g, 'a');
    str = str.replace(/Ä/g, 'A');
    str = str.replace(/!|%|\Ë†|\Ëœ|\â‚¬|\Â£|\Â¥|\â€¢|\â‚«|\^|\*|\(|\)|\+|\=|\<|\>|\?|\/|,|\:|\;|\'|\"|\&|\#|\[|\]|~|\$|`{|}|\||\\/g, '');
    str = str.replace(/¡|£|¢|Ä|ƒ|ÄƑ|ä/g, '');
    str = str.replace(/¨|©|ª|»/g, '');
    str = str.replace(/¬|‹|‰|Ä/g, '');
    str = str.replace(/²|³||´|“|‘|™|•|—|Æ|›|µ|æ/g, '');
    str = str.replace(/¹|º|¥|§|å|°|«|±|¯/g, '');
    str = str.replace(/½|·|‘|æ|€/g, '');
    str = str.replace(/Õ|Ò|õ|ò/g, 'o');
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, 'a');
    str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, 'A');
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, 'e');
    str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, 'E');
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, 'i');
    str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, 'I');
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, 'o');
    str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, 'O');
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, 'u');
    str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, 'U');
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, 'y');
    str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, 'Y');
    str = str.replace(/đ/g, 'd');
    str = str.replace(/Đ/g, 'D');
    str = str.trim();
    return str;
  }

  validatePassword(password: string): boolean {
    const pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    const regex = new RegExp(pattern);
    return regex.test(password);
  }

  checkCheckBoxvalue(event: any) {
    this.isChecked = event.target.checked;
  }
}