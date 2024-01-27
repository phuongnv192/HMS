import { Component, OnInit } from "@angular/core";
import { AuthService } from "../services/auth.service";
import { CleanerService } from "../services/cleaner.service";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-profile",
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.scss"],
})
export class ProfileComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private cleanService: CleanerService,
    private route: ActivatedRoute
  ) {}
  profileData: any;
  historyData: any;

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      let id = params.get("id");
      this.cleanService.getEmployeeById(id).subscribe((res) => {
        this.profileData = res;
      });
    });
  }
}