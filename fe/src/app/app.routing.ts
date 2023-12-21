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
import { AuthGuard } from "./shared/auth.guard";
import { BookingManagementComponent } from "./employee/booking-management/booking-management.component";
import { SupportHelpComponent } from "./auth/support-help/support.component";

const routes: Routes = [
  { path: 'home', component: HomeComponent  },
  { path: 'profile/:id', component: ProfileComponent , canActivate: [AuthGuard] },
  { path: 'register', component: SignupComponent},
  { path: 'introduction', component: IntroductionComponent , canActivate: [AuthGuard] },
  { path: 'booking', component: BookingComponent , canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent  },
  { path: 'forgot-password', component: ForgotPWComponent , canActivate: [AuthGuard] },
  { path: 'dashboard', component: DashboardComponent , canActivate: [AuthGuard] },
  { path: 'list-cleaner', component: ListCleanerComponent , canActivate: [AuthGuard] },
  { path: 'list-customer', component: ListCustomerComponent , canActivate: [AuthGuard] },
  { path: 'user-profile', component: UserProfileComponent , canActivate: [AuthGuard] },
  { path: 'customer-profile', component: CustomerProfileComponent , canActivate: [AuthGuard] },
  { path: 'history', component: HistoryComponent , canActivate: [AuthGuard] },
  { path: 'customer-history', component: CustomerHistoryComponent , canActivate: [AuthGuard] },
  { path: 'schedule', component: ScheduleComponent , canActivate: [AuthGuard] },
  { path: 'customer-schedule', component: CustomerScheduleComponent , canActivate: [AuthGuard] },
  { path: 'forgot-password', component: ForgotPWComponent , canActivate: [AuthGuard] },
  { path: 'change-password', component: ChangePassComponent , canActivate: [AuthGuard] },
  { path: 'booking-management', component: BookingManagementComponent , canActivate: [AuthGuard] },
  { path: 'support-help', component: SupportHelpComponent , canActivate: [AuthGuard] },
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