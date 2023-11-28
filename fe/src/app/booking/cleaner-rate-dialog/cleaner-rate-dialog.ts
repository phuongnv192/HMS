import { Subscription } from 'rxjs';
import { CleanerData } from '../booking.component';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';

// import { DialogService } from 'src/app/services/dialog.service';

@Component({
  selector: 'app-cleaner-rate-dialog',
  templateUrl: './cleaner-rate-dialog.html',
  styleUrls: ['./cleaner-rate-dialog.css']
})

export class CleanerRateDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public listCleaner: any;
  dataCleaner: any;

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public cleanerDialogRef: MatDialogRef<CleanerRateDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CleanerData) {
  }

  ngOnInit(): void {
    this.listCleaner = [];
  }


  onNoClick(): void {
    this.cleanerDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  cleanerRateDetail() {

  }
}