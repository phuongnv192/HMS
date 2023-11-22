import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { RouterModule } from "@angular/router";
import { AppRoutingModule } from "./app.routing";

import { AppComponent } from './app.component';
import { SignupComponent } from './auth/signup/signup.component';
import { IntroductionComponent } from './introduction/introduction.component';
import { ProfileComponent } from './profile/profile.component';
import { HomeComponent } from './home/home.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { FooterComponent } from './shared/footer/footer.component';
import { CalendarDialog } from './booking/calendar-dialog/calendar-dialog';
import { PickCleanerDialog } from './booking/pick-cleaner-dialog/pick-cleaner-dialog';
import { CleanerRateDialog } from './booking/cleaner-rate-dialog/cleaner-rate-dialog';
import { HomeModule } from './home/home.module';
import { LoginComponent } from './auth/login/login.component';
import { ForgotPWComponent } from './auth/forgot-password/forgot-pw.component';
import { TermAndConditionDialog, BookingComponent } from './booking/booking.component';
import { TermAndConditionsComponent } from './template/term-and-conditions/term-and-conditions.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { UserProfileComponent } from './employee/user-profile/user-profile.component';
import { DashboardComponent } from './employee/dashboard/dashboard.component';
import { HistoryComponent } from './employee/history/history.component';
import { ScheduleComponent } from './employee/schedule/schedule.component';

import { HomeModule } from "./home/home.module";
import { LoginComponent } from "./auth/login/login.component";
import { ForgotPWComponent } from "./auth/forgot-password/forgot-pw.component";
import { BookingComponent } from "./booking/booking.component";
import { SidebarComponent } from "./shared/sidebar/sidebar.component";
import { UserProfileComponent } from "./employee/user-profile/user-profile.component";
import { SignupComponent } from "./auth/signup/signup.component";
import { DashboardComponent } from "./employee/dashboard/dashboard.component";
import { TablesComponent } from "./employee/tables.component";
import { AuthService } from "./services/auth.service";
import { HttpClientModule } from "@angular/common/http";
import { IvyCarouselModule } from "angular-responsive-carousel";

@NgModule({
  declarations: [
    AppComponent,
    IntroductionComponent,
    BookingComponent,
    ProfileComponent,
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    LoginComponent,
    CalendarDialog,
    TermAndConditionsComponent,
    TermAndConditionDialog,
    PickCleanerDialog,
    CleanerRateDialog,
    ForgotPWComponent,
    UserProfileComponent,
    DashboardComponent,
    TablesComponent,
    SignupComponent,
    HistoryComponent,
    ScheduleComponent,
  ],
  imports: [
    BrowserModule,
    NgbModule,
    FormsModule,
    RouterModule,
    AppRoutingModule,
    HomeModule,
    HttpClientModule,
    IvyCarouselModule
  ],
  providers: [AuthService],
  bootstrap: [AppComponent],
  exports: [],
})
export class AppModule { }