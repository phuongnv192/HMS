import { Subscription } from 'rxjs';
import { PickCleanerData } from '../booking.component';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { DialogService } from 'src/app/services/dialog.service';
import { CleanerRateDialog } from '../cleaner-rate-dialog/cleaner-rate-dialog';


@Component({
  selector: 'app-pick-cleaner-dialog',
  templateUrl: './pick-cleaner-dialog.html',
  styleUrls: ['./pick-cleaner-dialog.css']
})

export class PickCleanerDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public listCleaner: any;
  dataCleaner: any;

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    private dialogService: DialogService,
    public dialogRef: MatDialogRef<CleanerRateDialog>,
    public cleanerDialogRef: MatDialogRef<PickCleanerDialog>,
    @Inject(MAT_DIALOG_DATA) public data: PickCleanerData) {
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
    this.dialogService.sendDataDialog(true);
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(CleanerRateDialog, {
      width: '400px',
      maxHeight: '75%',
      data: {
        data: this.dataCleaner,
      },
      panelClass: ['cleaner-detail']
    });

    this.dialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
      this.dialogService.sendDataDialog(false);
    });
  }

  searchCleaner() {

  }
}
