import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { AppRoutingModule } from "./app.routing";

import { AppComponent } from "./app.component";
import { IntroductionComponent } from "./introduction/introduction.component";
import { ProfileComponent } from "./profile/profile.component";
import { HomeComponent } from "./home/home.component";
import { NavbarComponent } from "./shared/navbar/navbar.component";
import { NavbarCustomerComponent } from "./shared/navbar-customer/navbar-customer.component";
import { FooterComponent } from "./shared/footer/footer.component";
import { CalendarDialog } from "./booking/calendar-dialog/calendar-dialog";
import { PickCleanerDialog } from "./booking/pick-cleaner-dialog/pick-cleaner-dialog";
import { CleanerRateDialog } from "./booking/cleaner-rate-dialog/cleaner-rate-dialog";
import {
  TermAndConditionDialog,
  PickCLeanerWarningDialog,
  BookingComponent,
} from "./booking/booking.component";
import { TermAndConditionsComponent } from "./template/term-and-conditions/term-and-conditions.component";
import { SidebarComponent } from "./shared/sidebar/sidebar.component";
import { UserProfileComponent } from "./employee/user-profile/user-profile.component";
import { DashboardComponent } from "./employee/dashboard/dashboard.component";
import { HistoryComponent } from "./employee/history/history.component";
import { ScheduleComponent } from "./employee/schedule/schedule.component";
import { BookingDetailDialog } from "./employee/schedule/booking-detail-dialog/booking-detail-dialog";

import { HomeModule } from "./home/home.module";
import { LoginComponent } from "./auth/login/login.component";
import { ForgotPWComponent } from "./auth/forgot-password/forgot-pw.component";
import { SignupComponent } from "./auth/signup/signup.component";
import { ListCleanerComponent } from "./employee/tables.component";
import { AuthService } from "./services/auth.service";
import { HTTP_INTERCEPTORS, HttpClientModule } from "@angular/common/http";
import { IvyCarouselModule } from "angular-responsive-carousel";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { ListCustomerComponent } from "./customer/list-customer/list-customer.component";
import { CustomerProfileComponent } from "./customer/customer-profile/customer-profile.component";
import { ChangePassComponent } from "./auth/change-password/change-password.component";
import { CustomerHistoryComponent } from "./customer/customer-booking/customer-booking-history/customer-history.component";
import { CustomerScheduleComponent } from "./customer/customer-booking/customer-booking-list/customer-schedule.component";
import { AuthInterceptor } from "./core/authenticator";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { PriceListDialog } from "./booking/price-list-dialog/price-list-dialog";
import { PriceFormatPipe } from "./pipe/currency.pipe";
import { AddServiceDialog } from "./booking/calendar-dialog/add-service-dialog/add-service-dialog";
import { CleanerDetailDialog } from "./booking/pick-cleaner-dialog/cleaner-detail-dialog/cleaner-detail-dialog";
import { CacheService } from "./services/cache.service";
import { BookingService } from "./services/booking.service";
import { ToastrModule } from "ngx-toastr";
import { BrowserModule } from "@angular/platform-browser";
import { RouterModule } from '@angular/router';

// import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    IntroductionComponent,
    BookingComponent,
    ProfileComponent,
    NavbarComponent,
    NavbarCustomerComponent,
    SidebarComponent,
    FooterComponent,
    LoginComponent,
    CalendarDialog,
    TermAndConditionsComponent,
    TermAndConditionDialog,
    PickCLeanerWarningDialog,
    PickCleanerDialog,
    CleanerRateDialog,
    ForgotPWComponent,
    UserProfileComponent,
    DashboardComponent,
    ListCleanerComponent,
    SignupComponent,
    HistoryComponent,
    ScheduleComponent,
    BookingDetailDialog,
    ListCustomerComponent,
    CustomerProfileComponent,
    ChangePassComponent,
    CustomerHistoryComponent,
    CustomerScheduleComponent,
    PriceListDialog,
    PriceFormatPipe,
    AddServiceDialog,
    CleanerDetailDialog,
  ],
  imports: [
    NgbModule,
    FormsModule,
    AppRoutingModule,
    HomeModule,
    HttpClientModule,
    IvyCarouselModule,
    MatDialogModule,
    RouterModule,
    BrowserModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 30000, // 15 seconds
      closeButton: true,
      progressBar: true,
      preventDuplicates: true,
    }),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
    AuthService,
    CacheService,
    BookingService,
    { provide: MatDialogRef, useValue: {} },
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    BookingDetailDialog,
    CalendarDialog,
    TermAndConditionDialog,
    PickCleanerDialog,
    CleanerRateDialog,
  ],
  exports: [],
})
export class AppModule {}