import { Subscription } from 'rxjs';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
// import { DialogService } from 'src/app/services/dialog.service';

export interface ChangeStatusDialogData {
  data: any;
  addService: any;
}

@Component({
  selector: 'app-change-status-dialog',
  templateUrl: './change-status-dialog.html',
  styleUrls: ['./change-status-dialog.scss']
})

export class ChangeStatusDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public listCleaner: any;
  dataCleaner: any;
  selectedCleaner: any;
  listA: any;
  listB: any;
  listPick: any;
  listPickData = [];
  searchname = '';
  noDataSearch = false;
  searchResults: any[];
  listPickNameData: any;
  dataPickInfo: { listPickData: any[]; listPickDataName: any; };

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    private statusDialogRef: MatDialogRef<ChangeStatusDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeStatusDialogData) {
  }

  ngOnInit(): void {
   
  }

  filterCleaners(): void {
    this.listPick = this.listCleaner.filter(cleaner => this.listPickData.includes(cleaner.cleanerId));
  }


  onNoClick(): void {
    this.statusDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }



  submit() {
    
  }
}