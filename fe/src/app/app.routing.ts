import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { BrowserModule } from "@angular/platform-browser";
import { Routes, RouterModule } from "@angular/router";

import { HomeComponent } from "./home/home.component";
import { ProfileComponent } from "./profile/profile.component";
import { SignupComponent } from "./auth/signup/signup.component";
import { IntroductionComponent } from "./introduction/introduction.component";
import { LoginComponent } from "./auth/login/login.component";
import { BookingComponent } from "./booking/booking.component";
import { DashboardComponent } from "./employee/dashboard/dashboard.component";
import { ListCleanerComponent } from "./employee/tables.component";
import { ForgotPWComponent } from "./auth/forgot-password/forgot-pw.component";
import { UserProfileComponent } from "./employee/user-profile/user-profile.component";
import { HistoryComponent } from "./employee/history/history.component";
import { ScheduleComponent } from "./employee/schedule/schedule.component";
import { ListCustomerComponent } from "./customer/list-customer/list-customer.component";
import { CustomerProfileComponent } from "./customer/customer-profile/customer-profile.component";
import { ChangePassComponent } from "./auth/change-password/change-password.component";
import { CustomerScheduleComponent } from "./customer/customer-booking/customer-booking-list/customer-schedule.component";
import { CustomerHistoryComponent } from "./customer/customer-booking/customer-booking-history/customer-history.component";

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'register', component: SignupComponent },
  { path: 'introduction', component: IntroductionComponent },
  { path: 'booking', component: BookingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPWComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'list-cleaner', component: ListCleanerComponent },
  { path: 'list-customer', component: ListCustomerComponent },
  { path: 'user-profile', component: UserProfileComponent },
  { path: 'customer-profile', component: CustomerProfileComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'customer-history', component: CustomerHistoryComponent },
  { path: 'schedule', component: ScheduleComponent },
  { path: 'customer-schedule', component: CustomerScheduleComponent },
  { path: 'forgot-password', component: ForgotPWComponent },
  { path: 'change-password', component: ChangePassComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes)
  ],
  exports: [
  ],
})
export class AppRoutingModule { }