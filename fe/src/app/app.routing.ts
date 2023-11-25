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
import { TablesComponent } from "./employee/tables.component";
import { ForgotPWComponent } from "./auth/forgot-password/forgot-pw.component";
import { UserProfileComponent } from "./employee/user-profile/user-profile.component";
import { HistoryComponent } from "./employee/history/history.component";
import { ScheduleComponent } from "./employee/schedule/schedule.component";

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'register', component: SignupComponent },
  { path: 'introduction', component: IntroductionComponent },
  { path: 'booking', component: BookingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPWComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'list-user', component: TablesComponent },
  { path: 'user-profile', component: UserProfileComponent },
  { path: 'history', component: HistoryComponent },
  { path: 'schedule', component: ScheduleComponent },
  { path: 'forgot-password',          component: ForgotPWComponent },
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